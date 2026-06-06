/**
 * 错误页插画 — 与 _reference/prototype.html ERR_ILLU 保持一致
 */
export const ERR_ILLU = {
  404: `
      <svg viewBox="0 0 220 180" xmlns="http://www.w3.org/2000/svg">
        <ellipse cx="110" cy="160" rx="56" ry="6" fill="rgba(0,0,0,.25)"/>
        <g class="rotate-slow">
          <circle cx="110" cy="80" r="60" fill="url(#g404outer)" stroke="rgba(212,162,76,.5)" stroke-width="1.5"/>
          <circle cx="110" cy="80" r="48" fill="none" stroke="rgba(255,255,255,.18)" stroke-width="1" stroke-dasharray="2 4"/>
        </g>
        <g stroke="rgba(212,162,76,.7)" stroke-width="2" stroke-linecap="round">
          <line x1="110" y1="14" x2="110" y2="24"/>
          <line x1="110" y1="136" x2="110" y2="146"/>
          <line x1="44" y1="80" x2="54" y2="80"/>
          <line x1="166" y1="80" x2="176" y2="80"/>
        </g>
        <g>
          <polygon points="110,38 118,80 110,122 102,80" fill="url(#gNeedleN)"/>
          <polygon points="110,38 118,80 110,80" fill="#F1E0BD" opacity=".9"/>
          <circle cx="110" cy="80" r="6" fill="#0B1E47" stroke="#F1E0BD" stroke-width="2"/>
        </g>
        <text x="160" y="56" font-family="Georgia,serif" font-size="44" font-weight="800" fill="#F1E0BD" opacity=".55">?</text>
        <text x="46" y="124" font-family="Georgia,serif" font-size="32" font-weight="800" fill="#F1E0BD" opacity=".4">?</text>
        <defs>
          <radialGradient id="g404outer" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stop-color="rgba(212,162,76,.18)"/>
            <stop offset="100%" stop-color="rgba(11,30,71,.5)"/>
          </radialGradient>
          <linearGradient id="gNeedleN" x1="0" x2="0" y1="0" y2="1">
            <stop offset="0%" stop-color="#F87171"/>
            <stop offset="50%" stop-color="#F1E0BD"/>
            <stop offset="50.01%" stop-color="#94A3B8"/>
            <stop offset="100%" stop-color="#64748B"/>
          </linearGradient>
        </defs>
      </svg>`,
  network: `
      <svg viewBox="0 0 220 180" xmlns="http://www.w3.org/2000/svg">
        <ellipse cx="110" cy="160" rx="50" ry="5" fill="rgba(0,0,0,.25)"/>
        <g fill="none" stroke="url(#gWifi)" stroke-width="6" stroke-linecap="round" opacity=".9">
          <path d="M50 100 Q110 38 170 100" />
          <path d="M68 116 Q110 70 152 116" opacity=".7"/>
          <path d="M86 132 Q110 102 134 132" opacity=".5"/>
        </g>
        <circle cx="110" cy="140" r="8" fill="#F1E0BD" class="blink-slow"/>
        <line x1="40" y1="38" x2="180" y2="155" stroke="#F87171" stroke-width="5" stroke-linecap="round"/>
        <line x1="40" y1="38" x2="180" y2="155" stroke="#FCA5A5" stroke-width="1.5" stroke-linecap="round" opacity=".7"/>
        <circle cx="36" cy="58" r="3" fill="#F1E0BD" opacity=".5"/>
        <circle cx="186" cy="56" r="2" fill="#F87171" opacity=".7"/>
        <circle cx="170" cy="50" r="1.5" fill="#F1E0BD" opacity=".4"/>
        <defs>
          <linearGradient id="gWifi" x1="0" x2="1" y1="0" y2="0">
            <stop offset="0%" stop-color="#F1E0BD"/>
            <stop offset="100%" stop-color="#D4A24C"/>
          </linearGradient>
        </defs>
      </svg>`,
  server: `
      <svg viewBox="0 0 220 180" xmlns="http://www.w3.org/2000/svg">
        <ellipse cx="110" cy="160" rx="56" ry="6" fill="rgba(0,0,0,.25)"/>
        <g class="rotate-slow" style="transform-origin:80px 90px">
          <path d="M80,40 L84,32 L92,32 L96,40 L106,42 L114,36 L120,42 L114,52 L116,62 L124,66 L124,74 L116,78 L114,88 L120,98 L114,104 L106,98 L96,100 L92,108 L84,108 L80,100 L70,98 L62,104 L56,98 L62,88 L60,78 L52,74 L52,66 L60,62 L62,52 L56,42 L62,36 L70,42 Z"
                fill="url(#gGearBig)" stroke="rgba(212,162,76,.6)" stroke-width="1.2"/>
          <circle cx="80" cy="70" r="14" fill="#0B1E47" stroke="rgba(212,162,76,.7)" stroke-width="2"/>
          <circle cx="80" cy="70" r="5" fill="#F1E0BD"/>
        </g>
        <g class="rotate-slow" style="transform-origin:148px 124px;animation-duration:5s;animation-direction:reverse">
          <path d="M148,98 L150,92 L156,92 L158,98 L164,100 L170,96 L174,100 L170,108 L172,114 L178,118 L178,124 L172,128 L170,134 L174,142 L170,146 L164,142 L158,144 L156,150 L150,150 L148,144 L142,142 L138,146 L134,142 L138,134 L136,128 L130,124 L130,118 L136,114 L138,108 L134,100 L138,96 L142,100 Z"
                fill="url(#gGearSmall)" stroke="rgba(239,68,68,.5)" stroke-width="1"/>
          <circle cx="154" cy="120" r="9" fill="#0B1E47" stroke="rgba(239,68,68,.7)" stroke-width="1.5"/>
          <circle cx="154" cy="120" r="3" fill="#F87171" class="blink-slow"/>
        </g>
        <g fill="#F87171" class="blink-slow">
          <circle cx="120" cy="50" r="2"/>
          <circle cx="190" cy="80" r="2.5"/>
          <circle cx="40" cy="40" r="1.5"/>
          <circle cx="35" cy="130" r="2"/>
        </g>
        <defs>
          <radialGradient id="gGearBig" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stop-color="rgba(212,162,76,.4)"/>
            <stop offset="100%" stop-color="rgba(11,30,71,.6)"/>
          </radialGradient>
          <radialGradient id="gGearSmall" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stop-color="rgba(239,68,68,.3)"/>
            <stop offset="100%" stop-color="rgba(11,30,71,.5)"/>
          </radialGradient>
        </defs>
      </svg>`
};
