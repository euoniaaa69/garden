package com.example.ui.render

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.TimeOfDay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Advanced Celestial Bloom Shader Engine.
 *
 * Implements radiant, high-dynamic-range (HDR) atmospheric bloom, chromatic dispersion,
 * and Rayleigh/Mie optical scattering for the Sun and Moon across all diurnal cycles.
 *
 * Features:
 * - Multi-pass exponential radiance falloff with additive/screen blending.
 * - Dynamic diurnal bloom profiles (Dawn rose-gold, Midday incandescent solar, Sunset burning crimson, Night pearlescent silver-indigo).
 * - Chromatic aberration fringe rings and anamorphic light bleed.
 * - AGSL RuntimeShader acceleration on Android 13+ (API 33+) with fallback to multi-pass canvas shader pipelines on all Android devices.
 */
object CelestialBloomShader {

    // -------------------------------------------------------------------------
    // AGSL Shader Source for Android 13+ (Tiramisu API 33+)
    // -------------------------------------------------------------------------
    private const val AGSL_CELESTIAL_BLOOM = """
        uniform float2 uResolution;
        uniform float2 uCenter;
        uniform float uRadius;
        uniform float uIntensity;
        uniform float uTime;
        uniform float uChromatic;
        uniform half4 uInnerColor;
        uniform half4 uOuterColor;
        uniform half4 uMidColor;

        half4 main(float2 fragCoord) {
            float2 diff = fragCoord - uCenter;
            float dist = length(diff);
            
            // Normalized radius distance
            float normDist = dist / max(uRadius, 1.0);
            if (normDist > 2.5) {
                return half4(0.0, 0.0, 0.0, 0.0);
            }
            
            // Multi-tier exponential radiance falloff (HDR curve)
            float coreGlow = exp(-normDist * 4.5) * 1.4;
            float midGlow = exp(-normDist * 2.0) * 0.7;
            float outerGlow = exp(-normDist * 0.9) * 0.35;
            
            // Subtle atmospheric wave pulsation
            float angle = atan(diff.y, diff.x);
            float shimmer = sin(angle * 6.0 + uTime * 2.0) * 0.06 + cos(normDist * 8.0 - uTime * 3.0) * 0.04;
            
            float totalRadiance = (coreGlow + midGlow + outerGlow) * (1.0 + shimmer) * uIntensity;
            
            // Chromatic aberration color mixing
            float rDist = length(diff * (1.0 + uChromatic * 0.08)) / max(uRadius, 1.0);
            float bDist = length(diff * (1.0 - uChromatic * 0.08)) / max(uRadius, 1.0);
            
            half4 color;
            if (normDist < 0.4) {
                color = mix(uInnerColor, uMidColor, normDist / 0.4);
            } else {
                color = mix(uMidColor, uOuterColor, clamp((normDist - 0.4) / 1.6, 0.0, 1.0));
            }
            
            // Apply chromatic split to red and blue channels
            color.r *= exp(-rDist * 2.2) * 1.5 + 0.2;
            color.b *= exp(-bDist * 2.2) * 1.5 + 0.2;
            
            float alpha = clamp(totalRadiance * color.a, 0.0, 1.0);
            return half4(color.rgb * alpha, alpha);
        }
    """

    private var cachedRuntimeShader: RuntimeShader? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                cachedRuntimeShader = RuntimeShader(AGSL_CELESTIAL_BLOOM)
            } catch (ignored: Throwable) {
                cachedRuntimeShader = null
            }
        }
    }

    /**
     * Renders a radiant, multi-pass atmospheric bloom shader effect around the celestial body.
     */
    fun drawBloom(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        isSun: Boolean,
        timeOfDay: TimeOfDay,
        animTime: Float,
        ambientLight: Float = 1.0f
    ) {
        val config = getBloomConfig(isSun, timeOfDay, animTime, ambientLight)

        // Try hardware AGSL shader on Android 13+ if available
        val agslRendered = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && cachedRuntimeShader != null) {
            tryRenderAgslBloom(drawScope, cx, cy, config, animTime)
        } else {
            false
        }

        // Render high-fidelity multi-pass canvas HDR bloom (works independently & complements AGSL)
        renderMultiPassHdrBloom(drawScope, cx, cy, config, isSun, timeOfDay, animTime)
    }

    private fun tryRenderAgslBloom(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        config: BloomConfig,
        animTime: Float
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        val shader = cachedRuntimeShader ?: return false

        return try {
            val bloomRadius = config.outerRadius * 1.25f
            shader.setFloatUniform("uResolution", drawScope.size.width, drawScope.size.height)
            shader.setFloatUniform("uCenter", cx, cy)
            shader.setFloatUniform("uRadius", config.baseRadius)
            shader.setFloatUniform("uIntensity", config.intensity)
            shader.setFloatUniform("uTime", animTime)
            shader.setFloatUniform("uChromatic", config.chromaticDispersion)
            shader.setColorUniform("uInnerColor", android.graphics.Color.valueOf(
                config.innerGlow.red, config.innerGlow.green, config.innerGlow.blue, config.innerGlow.alpha
            ))
            shader.setColorUniform("uMidColor", android.graphics.Color.valueOf(
                config.midGlow.red, config.midGlow.green, config.midGlow.blue, config.midGlow.alpha
            ))
            shader.setColorUniform("uOuterColor", android.graphics.Color.valueOf(
                config.outerGlow.red, config.outerGlow.green, config.outerGlow.blue, config.outerGlow.alpha
            ))

            val brush = ShaderBrush(shader)
            drawScope.drawCircle(
                brush = brush,
                radius = bloomRadius,
                center = Offset(cx, cy),
                blendMode = BlendMode.Plus
            )
            true
        } catch (t: Throwable) {
            false
        }
    }

    private fun renderMultiPassHdrBloom(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        config: BloomConfig,
        isSun: Boolean,
        timeOfDay: TimeOfDay,
        animTime: Float
    ) {
        val pulse = config.pulse

        // ---------------------------------------------------------------------
        // Pass 1: Outer Atmospheric Scatter / Extended Rayleigh Bloom
        // ---------------------------------------------------------------------
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    config.outerGlow.copy(alpha = config.outerGlow.alpha * 0.9f),
                    config.outerGlow.copy(alpha = config.outerGlow.alpha * 0.45f),
                    config.outerGlow.copy(alpha = config.outerGlow.alpha * 0.15f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = config.outerRadius * pulse
            ),
            radius = config.outerRadius * pulse,
            center = Offset(cx, cy),
            blendMode = BlendMode.Screen
        )

        // ---------------------------------------------------------------------
        // Pass 2: Intermediate Chromatic Corona Diffusion Bloom
        // ---------------------------------------------------------------------
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    config.midGlow.copy(alpha = config.midGlow.alpha * 1.0f),
                    config.chromaticFringe.copy(alpha = config.chromaticFringe.alpha * 0.7f),
                    config.midGlow.copy(alpha = config.midGlow.alpha * 0.3f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = config.midRadius * pulse
            ),
            radius = config.midRadius * pulse,
            center = Offset(cx, cy),
            blendMode = BlendMode.Plus
        )

        // ---------------------------------------------------------------------
        // Pass 3: Core HDR Incandescent Photon Bloom
        // ---------------------------------------------------------------------
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    config.innerGlow.copy(alpha = (config.innerGlow.alpha * 1.1f).coerceAtMost(1f)),
                    config.innerGlow.copy(alpha = config.innerGlow.alpha * 0.8f),
                    config.midGlow.copy(alpha = config.midGlow.alpha * 0.4f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = config.coreBloomRadius * pulse
            ),
            radius = config.coreBloomRadius * pulse,
            center = Offset(cx, cy),
            blendMode = BlendMode.Plus
        )

        // ---------------------------------------------------------------------
        // Pass 4: Chromatic Aberration Halo Ring
        // ---------------------------------------------------------------------
        if (config.chromaticDispersion > 0.1f) {
            val ringRadius = config.baseRadius * 1.45f * pulse
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        config.chromaticFringe.copy(alpha = 0.28f * config.intensity),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = ringRadius + 8f
                ),
                radius = ringRadius + 8f,
                center = Offset(cx, cy),
                blendMode = BlendMode.Screen
            )
        }

        // ---------------------------------------------------------------------
        // Pass 5: Anamorphic Horizontal / Radiant Optical Bleed
        // ---------------------------------------------------------------------
        if (isSun) {
            renderSolarAnamorphicBloom(drawScope, cx, cy, config, timeOfDay, animTime)
        } else {
            renderLunarAtmosphericDiffraction(drawScope, cx, cy, config, animTime)
        }
    }

    private fun renderSolarAnamorphicBloom(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        config: BloomConfig,
        timeOfDay: TimeOfDay,
        animTime: Float
    ) {
        val isSunset = timeOfDay == TimeOfDay.SUNSET || timeOfDay == TimeOfDay.GOLDEN_HOUR
        val isDawn = timeOfDay == TimeOfDay.DAWN

        // Horizontal atmospheric streak across the horizon
        val streakWidth = if (isSunset) drawScope.size.width * 0.85f else drawScope.size.width * 0.55f
        val streakHeight = if (isSunset) 7.5f else 4.5f

        val streakColor = if (isSunset) Color(0x66FF7043) else if (isDawn) Color(0x55FFAB91) else Color(0x44FFFDE7)

        drawScope.drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    streakColor,
                    streakColor.copy(alpha = streakColor.alpha * 0.5f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = streakWidth * 0.5f
            ),
            topLeft = Offset(cx - streakWidth * 0.5f, cy - streakHeight * 0.5f),
            size = Size(streakWidth, streakHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(streakHeight * 0.5f, streakHeight * 0.5f),
            blendMode = BlendMode.Plus
        )

        // 4-Point Subtle Optical Flare Star
        val flareRot = (animTime * 12f) % 360f
        drawScope.rotate(flareRot, Offset(cx, cy)) {
            val flareLen = config.baseRadius * 1.8f * config.pulse
            for (i in 0 until 2) {
                val angleRad = (i * 90f * PI / 180.0).toFloat()
                val p1x = cx + cos(angleRad) * flareLen
                val p1y = cy + sin(angleRad) * flareLen
                val p2x = cx - cos(angleRad) * flareLen
                val p2y = cy - sin(angleRad) * flareLen

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, config.innerGlow.copy(alpha = 0.45f), Color.Transparent),
                        start = Offset(p1x, p1y),
                        end = Offset(p2x, p2y)
                    ),
                    start = Offset(p1x, p1y),
                    end = Offset(p2x, p2y),
                    strokeWidth = 2.2f,
                    blendMode = BlendMode.Plus
                )
            }
        }
    }

    private fun renderLunarAtmosphericDiffraction(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        config: BloomConfig,
        animTime: Float
    ) {
        // Lunar 22° Halo Bloom with subtle prismatic refraction
        val haloRadius = config.baseRadius * 2.35f * config.pulse
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x22818CF8),
                    Color(0x1838BDF8),
                    Color(0x0AC084FC),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = haloRadius + 15f
            ),
            radius = haloRadius + 15f,
            center = Offset(cx, cy),
            blendMode = BlendMode.Screen
        )

        // Delicate rotating diamond cross glimmers
        val rotAngle = (sin(animTime.toDouble() * 0.8).toFloat() * 15f)
        drawScope.rotate(rotAngle, Offset(cx, cy)) {
            val sparkLen = config.baseRadius * 1.4f
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0x33E0F2FE), Color.Transparent),
                    start = Offset(cx, cy - sparkLen),
                    end = Offset(cx, cy + sparkLen)
                ),
                start = Offset(cx, cy - sparkLen),
                end = Offset(cx, cy + sparkLen),
                strokeWidth = 1.8f,
                blendMode = BlendMode.Plus
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0x33E0F2FE), Color.Transparent),
                    start = Offset(cx - sparkLen, cy),
                    end = Offset(cx + sparkLen, cy)
                ),
                start = Offset(cx - sparkLen, cy),
                end = Offset(cx + sparkLen, cy),
                strokeWidth = 1.8f,
                blendMode = BlendMode.Plus
            )
        }
    }

    private fun getBloomConfig(
        isSun: Boolean,
        timeOfDay: TimeOfDay,
        animTime: Float,
        ambientLight: Float
    ): BloomConfig {
        val pulse = (sin(animTime.toDouble() * 1.6).toFloat() * 0.05f + 1.0f)

        if (isSun) {
            return when (timeOfDay) {
                TimeOfDay.DAWN -> BloomConfig(
                    baseRadius = 32f,
                    coreBloomRadius = 45f,
                    midRadius = 90f,
                    outerRadius = 150f,
                    intensity = 1.05f,
                    chromaticDispersion = 0.45f,
                    pulse = pulse,
                    innerGlow = Color(0xFFFFF9C4),
                    midGlow = Color(0x88FFAB91),
                    outerGlow = Color(0x44FF80AB),
                    chromaticFringe = Color(0x55FFE082)
                )
                TimeOfDay.GOLDEN_HOUR, TimeOfDay.SUNSET -> BloomConfig(
                    baseRadius = 40f,
                    coreBloomRadius = 60f,
                    midRadius = 120f,
                    outerRadius = 210f,
                    intensity = 1.35f,
                    chromaticDispersion = 0.75f,
                    pulse = pulse * 1.05f,
                    innerGlow = Color(0xFFFFFDE7),
                    midGlow = Color(0xAAFF7043),
                    outerGlow = Color(0x66EA580C),
                    chromaticFringe = Color(0x88F59E0B)
                )
                TimeOfDay.DUSK -> BloomConfig(
                    baseRadius = 34f,
                    coreBloomRadius = 50f,
                    midRadius = 95f,
                    outerRadius = 160f,
                    intensity = 0.95f,
                    chromaticDispersion = 0.60f,
                    pulse = pulse,
                    innerGlow = Color(0xFFFFCC80),
                    midGlow = Color(0x77E11D48),
                    outerGlow = Color(0x447C3AED),
                    chromaticFringe = Color(0x55F43F5E)
                )
                else -> BloomConfig( // MORNING, NOON, AFTERNOON
                    baseRadius = 30f,
                    coreBloomRadius = 48f,
                    midRadius = 100f,
                    outerRadius = 175f,
                    intensity = 1.20f,
                    chromaticDispersion = 0.50f,
                    pulse = pulse,
                    innerGlow = Color(0xFFFFFFFF),
                    midGlow = Color(0x99FDE047),
                    outerGlow = Color(0x45F59E0B),
                    chromaticFringe = Color(0x66FFE082)
                )
            }
        } else {
            // MOON BLOOM (NIGHT / MIDNIGHT)
            return BloomConfig(
                baseRadius = 26f,
                coreBloomRadius = 38f,
                midRadius = 78f,
                outerRadius = 135f,
                intensity = 0.90f,
                chromaticDispersion = 0.35f,
                pulse = (sin(animTime.toDouble() * 1.3).toFloat() * 0.04f + 1.0f),
                innerGlow = Color(0xFFFFFFFF),
                midGlow = Color(0x77E0F2FE),
                outerGlow = Color(0x38818CF8),
                chromaticFringe = Color(0x44C7D2FE)
            )
        }
    }

    private data class BloomConfig(
        val baseRadius: Float,
        val coreBloomRadius: Float,
        val midRadius: Float,
        val outerRadius: Float,
        val intensity: Float,
        val chromaticDispersion: Float,
        val pulse: Float,
        val innerGlow: Color,
        val midGlow: Color,
        val outerGlow: Color,
        val chromaticFringe: Color
    )
}
