/**
 * S2GRUPO Presentation Builder Template
 *
 * Copy this file and adapt it for each presentation.
 * The helper functions (addBlueSlide, addWhiteSlide, addTerminal, addCard,
 * addSectionSlide) are the reusable framework. The SLIDE blocks at the
 * bottom are your content — replace them entirely.
 *
 * Usage:
 *   npm install -g pptxgenjs
 *   node build_presentation.js
 *   python scripts/inject_animations.py --input output.pptx --meta anim_meta.json --output output-animated.pptx
 *
 * Requirements:
 *   - logo_blue.b64 and logo_white.b64 in the working directory
 *     (copy from ${CLAUDE_PLUGIN_ROOT}/skills/pptx/assets/ before running)
 */

const pptxgen = require("pptxgenjs");
const fs = require("fs");

const pres = new pptxgen();
pres.layout = "LAYOUT_16x9"; // 10" × 5.625"
pres.author = "S2GRUPO";
pres.title = "Presentation Title"; // ← CHANGE THIS

// ═══════════════════════════════════════════════
// BRANDING TOKENS — S2GRUPO corporate identity V4
// ═══════════════════════════════════════════════
const BLUE = "0545b0";
const BLUE_LIGHT = "5a84ce";
const WHITE = "FFFFFF";
const CREAM = "ededeb";
const LIME = "dbf266";
const TEXT = "1A1A2E";
const TEXT_DIM = "5A5A7A";
const GREEN = "1A9A5A";
const AMBER = "C48800";
const RED = "CC2233";
const CYAN = "0077AA";

// Semaphoric (status indicators only)
const SEM_GREEN = "08ad66";
const SEM_YELLOW = "e8ed59";
const SEM_ORANGE = "ea9b31";
const SEM_RED = "c2262e";

// Terminal dark theme
const TERM_BG = "0D1117";
const TERM_HEADER = "161B22";
const TERM_GREEN = "3FB950";
const TERM_BLUE = "58A6FF";
const TERM_PURPLE = "BC8CFF";
const TERM_ORANGE = "D29922";
const TERM_RED = "F85149";
const TERM_CYAN = "39C5CF";
const TERM_DIM = "8B949E";
const TERM_TEXT = "E6EDF3";

// Fonts
const FONT = "Arial";
const MONO = "Consolas";

// ═══════════════════════════════════════════════
// LAYOUT CONSTANTS
// ═══════════════════════════════════════════════
const CONTENT_X = 0.4;       // Left margin for content
const CONTENT_W = 9.2;       // Full-width content area
const CONTENT_Y = 1.15;      // Content start Y (below title bar)
const COL_W = 4.3;           // Two-column width
const COL_GAP = 0.5;         // Gap between columns
const COL_R_X = CONTENT_X + COL_W + COL_GAP; // Right column X = 5.1

// Box padding — vertical breathing room for text inside shapes
// [top, right, bottom, left] in points
const BOX_MARGIN = [8, 6, 8, 6]; // ~0.11" top/bottom — avoids text touching edges
const BOX_MIN_H = 0.55;          // inches — minimum box height to fit one text line + padding

// Terminal sizing
const TERM_FONT_SIZE = 7;    // pt — Consolas (reduced from 8pt for overflow safety)
const TERM_LINE_H = 0.15;    // inches per line (fits ~24 lines in h=3.9")
const TERM_HEADER_H = 0.28;  // header bar height
const TERM_BODY_PAD = 0.14;  // left/right padding inside terminal body
const TERM_BODY_START = 0.33; // offset from terminal top Y to first line Y

// Logos (base64)
const logoBlueB64 = "image/png;base64," + fs.readFileSync("logo_blue.b64", "utf8");
const logoWhiteB64 = "image/png;base64," + fs.readFileSync("logo_white.b64", "utf8");

// ═══════════════════════════════════════════════
// ANIMATION TRACKING
// Do not modify — used by inject_animations.py
// ═══════════════════════════════════════════════
const terminalAnimations = {};
let termLineCounter = 0;

// ═══════════════════════════════════════════════
// HELPER FUNCTIONS — the reusable framework
// ═══════════════════════════════════════════════

/** Fresh shadow object (pptxgenjs mutates these — never reuse) */
const mkShadow = () => ({
  type: "outer", blur: 4, offset: 2,
  color: "000000", opacity: 0.10, angle: 135
});

/**
 * Blue background slide with S2GRUPO branding.
 * Use for: title, section dividers, closing.
 */
function addBlueSlide() {
  const s = pres.addSlide();
  s.background = { color: BLUE };
  s.addImage({ data: logoWhiteB64, x: 0.35, y: 0.25, w: 1.2, h: 0.21 });
  s.addText("Anticipando un mundo ciberseguro", {
    x: 7.5, y: 0.25, w: 2.2, h: 0.25,
    fontSize: 7, fontFace: FONT, color: WHITE,
    italic: true, align: "right", transparency: 30
  });
  s.addText("© 2026, S2GRUPO. Todos los derechos reservados.", {
    x: 5, y: 5.3, w: 4.7, h: 0.2,
    fontSize: 6, fontFace: FONT, color: WHITE,
    align: "right", transparency: 50
  });
  return s;
}

/**
 * White background content slide with blue title bar.
 * @param {string} title - Title text shown in the blue bar
 * @param {string} [barSubtitle] - Optional right-aligned subtitle in bar
 */
function addWhiteSlide(title, barSubtitle) {
  const s = pres.addSlide();
  s.background = { color: WHITE };
  s.addImage({ data: logoBlueB64, x: 0.35, y: 0.18, w: 1.2, h: 0.21 });
  s.addText("Anticipando un mundo ciberseguro", {
    x: 7.5, y: 0.2, w: 2.2, h: 0.25,
    fontSize: 7, fontFace: FONT, color: TEXT_DIM,
    italic: true, align: "right"
  });
  // Blue title bar
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0.55, w: 10, h: 0.48, fill: { color: BLUE }
  });
  s.addText(title, {
    x: 0.4, y: 0.55, w: barSubtitle ? 7.5 : 9.2, h: 0.48,
    fontSize: 16, fontFace: FONT, color: WHITE,
    bold: true, valign: "middle", margin: 0,
    autoFit: true
  });
  if (barSubtitle) {
    s.addText(barSubtitle, {
      x: 7.5, y: 0.55, w: 2.2, h: 0.48,
      fontSize: 10, fontFace: FONT, color: WHITE,
      transparency: 30, align: "right", valign: "middle",
      margin: [0, 10, 0, 0],
      autoFit: true
    });
  }
  // Footer
  s.addText("© 2026, S2GRUPO. Todos los derechos reservados.", {
    x: 5, y: 5.3, w: 4.7, h: 0.2,
    fontSize: 6, fontFace: FONT, color: TEXT_DIM, align: "right"
  });
  return s;
}

/**
 * Blue section divider slide with optional tag badge.
 * @param {string|null} tag - Small badge text (e.g., "Bloque 1")
 * @param {string} title - Large title (supports \n for line breaks)
 * @param {string} [subtitle] - Smaller subtitle text
 */
function addSectionSlide(tag, title, subtitle) {
  const s = addBlueSlide();
  if (tag) {
    const tagW = tag.length * 0.11 + 0.3;
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
      x: 0.45, y: 1.3, w: tagW, h: 0.3,
      fill: { color: WHITE, transparency: 85 },
      line: { color: WHITE, width: 0.5, transparency: 75 },
      rectRadius: 0.03
    });
    s.addText(tag, {
      x: 0.45, y: 1.3, w: tagW, h: 0.3,
      fontSize: 9, fontFace: FONT, color: WHITE,
      bold: true, align: "center", valign: "middle", margin: 0
    });
  }
  s.addText(title, {
    x: 0.45, y: 1.7, w: 6, h: 1.6,
    fontSize: 32, fontFace: FONT, color: WHITE,
    bold: true, valign: "top", margin: 0,
    autoFit: true
  });
  if (subtitle) {
    s.addText(subtitle, {
      x: 0.45, y: 3.4, w: 5.5, h: 0.6,
      fontSize: 14, fontFace: FONT, color: WHITE,
      transparency: 25, margin: 0,
      autoFit: true
    });
  }
  return s;
}

/**
 * Dark terminal block with header dots and animated lines.
 *
 * Each non-empty line becomes a separate text box for animation.
 * The animation metadata is tracked in terminalAnimations{} and
 * exported to anim_meta.json for the Python post-processor.
 *
 * @param {object} slide - The slide to add the terminal to
 * @param {number} x - Left position in inches
 * @param {number} y - Top position in inches
 * @param {number} w - Width in inches
 * @param {number} h - Height in inches
 * @param {string} titleText - Text shown in the terminal header
 * @param {Array} lines - Array of line content. Each line is one of:
 *   - "" (empty string) → spacer, no shape created
 *   - "text" (string) → plain terminal text
 *   - [{text, color?, bold?}, ...] → rich colored segments
 */
function addTerminal(slide, x, y, w, h, titleText, lines) {
  const slideIdx = pres.slides.length;

  // Background
  slide.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x, y, w, h,
    fill: { color: TERM_BG }, rectRadius: 0.06,
    shadow: mkShadow()
  });
  // Header bar
  slide.addShape(pres.shapes.RECTANGLE, {
    x, y, w, h: 0.28, fill: { color: TERM_HEADER }
  });
  // Traffic light dots
  const dy = y + 0.1;
  slide.addShape(pres.shapes.OVAL, { x: x + 0.12, y: dy, w: 0.09, h: 0.09, fill: { color: "F85149" } });
  slide.addShape(pres.shapes.OVAL, { x: x + 0.25, y: dy, w: 0.09, h: 0.09, fill: { color: "D29922" } });
  slide.addShape(pres.shapes.OVAL, { x: x + 0.38, y: dy, w: 0.09, h: 0.09, fill: { color: "3FB950" } });
  // Header title
  slide.addText(titleText, {
    x: x + 0.52, y: y + 0.02, w: w - 0.6, h: 0.25,
    fontSize: 7, fontFace: MONO, color: TERM_DIM,
    valign: "middle", margin: 0
  });

  // Body lines — each as its own text box for animation
  // Max lines ≈ (h - TERM_BODY_START) / TERM_LINE_H
  // Max chars ≈ ~110 at 7pt Consolas in 9.2" terminal (bodyW ≈ 8.92")
  const bodyX = x + TERM_BODY_PAD;
  const bodyW = w - TERM_BODY_PAD * 2;
  const lineH = TERM_LINE_H;
  const startY = y + TERM_BODY_START;

  if (!terminalAnimations[slideIdx]) terminalAnimations[slideIdx] = [];

  lines.forEach((line, i) => {
    const lineY = startY + i * lineH;
    const name = `termline_${termLineCounter++}`;

    // Skip empty spacer lines
    if (typeof line === "string" && line === "") return;

    let richText;
    if (typeof line === "string") {
      richText = [{ text: line, options: { color: TERM_TEXT, fontSize: TERM_FONT_SIZE, fontFace: MONO } }];
    } else {
      richText = line.map(seg => ({
        text: seg.text,
        options: {
          color: seg.color || TERM_TEXT,
          fontSize: TERM_FONT_SIZE, fontFace: MONO,
          bold: seg.bold || false
        }
      }));
    }

    slide.addText(richText, {
      x: bodyX, y: lineY, w: bodyW, h: lineH,
      valign: "middle", margin: 0,
      autoFit: true,
      objectName: name
    });

    terminalAnimations[slideIdx].push({
      name,
      delay: i === 0 ? 0 : 400,
      isFirst: i === 0
    });
  });
}

/**
 * White card with colored left accent bar and shadow.
 *
 * @param {object} slide - The slide to add the card to
 * @param {number} x - Left position
 * @param {number} y - Top position
 * @param {number} w - Width
 * @param {number} h - Height
 * @param {string} accentColor - Hex color for the left bar (e.g., BLUE, RED, GREEN)
 * @param {string|Array} content - Plain string or pptxgenjs rich text array
 */
function addCard(slide, x, y, w, h, accentColor, content) {
  const cardH = Math.max(h, BOX_MIN_H);
  slide.addShape(pres.shapes.RECTANGLE, {
    x, y, w, h: cardH,
    fill: { color: WHITE },
    line: { color: "E0E4EA", width: 0.5 },
    shadow: mkShadow()
  });
  // Accent bar
  slide.addShape(pres.shapes.RECTANGLE, {
    x, y, w: 0.04, h: cardH, fill: { color: accentColor }
  });
  // Content — use physical offsets for padding so autoFit cannot eat them.
  // The text box is inset from the card edges; margin is a small safety net only.
  const textX = x + 0.16;       // accent bar (0.04) + 0.12 breathing room
  const textY = y + 0.06;       // physical top padding
  const textW = w - 0.30;       // 0.16 left + 0.14 right
  const textH = cardH - 0.12;   // 0.06 top + 0.06 bottom
  if (typeof content === "string") {
    slide.addText(content, {
      x: textX, y: textY, w: textW, h: textH,
      fontSize: 9, fontFace: FONT, color: TEXT,
      valign: "middle", margin: [2, 2, 2, 2],
      autoFit: true
    });
  } else {
    slide.addText(content, {
      x: textX, y: textY, w: textW, h: textH,
      valign: "middle", margin: [2, 2, 2, 2],
      autoFit: true
    });
  }
}

/**
 * Body paragraph with auto-fit. Use for narrative text below the title bar.
 * Returns the Y position after the paragraph for easy chaining.
 *
 * @param {object} slide - The slide to add text to
 * @param {string|Array} text - Plain string or pptxgenjs rich text array
 * @param {object} [opts] - Override defaults: { x, y, w, h, fontSize, color, lineSpacing, align }
 * @returns {number} Next Y position (y + h) for layout chaining
 */
function addParagraph(slide, text, opts = {}) {
  const x = opts.x ?? CONTENT_X;
  const y = opts.y ?? CONTENT_Y;
  const w = opts.w ?? CONTENT_W;
  const h = opts.h ?? 0.6;
  const config = {
    x, y, w, h,
    fontSize: opts.fontSize ?? 10,
    fontFace: FONT,
    color: opts.color ?? TEXT,
    valign: opts.valign ?? "top",
    align: opts.align ?? "left",
    margin: 0,
    autoFit: true,
    lineSpacingMultiple: opts.lineSpacing ?? 1.15
  };
  slide.addText(text, config);
  return y + h;
}


// ═══════════════════════════════════════════════
// YOUR SLIDES START HERE
// Replace everything below with your content.
// The examples show how to use each component.
// ═══════════════════════════════════════════════

// ── EXAMPLE: Title slide ──
const s0 = addBlueSlide();
s0.addShape(pres.shapes.ROUNDED_RECTANGLE, {
  x: 0.45, y: 1.5, w: 1.2, h: 0.3,
  fill: { color: WHITE, transparency: 85 },
  line: { color: WHITE, width: 0.5, transparency: 75 },
  rectRadius: 0.03
});
s0.addText("enigma.dev", {
  x: 0.45, y: 1.5, w: 1.2, h: 0.3,
  fontSize: 9, fontFace: FONT, color: WHITE,
  bold: true, align: "center", valign: "middle", margin: 0
});
s0.addText("Presentation Title\nSubtitle Here", {
  x: 0.45, y: 1.9, w: 6, h: 2,
  fontSize: 40, fontFace: FONT, color: WHITE,
  bold: false, lineSpacingMultiple: 1.05, margin: 0
});
s0.addText("A short description of what this presentation covers", {
  x: 0.45, y: 3.9, w: 5, h: 0.6,
  fontSize: 13, fontFace: FONT, color: WHITE,
  transparency: 20, margin: 0
});

// ── EXAMPLE: Section divider ──
addSectionSlide("Bloque 1", "Section Title\nWith Two Lines", "Short subtitle or description");

// ── EXAMPLE: Content slide with cards ──
// Default accent = BLUE. Use AMBER for secondary variety.
// RED/GREEN ONLY for explicit before/after or good/bad comparisons.
const sCards = addWhiteSlide("Cards Example");
addCard(sCards, 0.4, 1.2, 4.3, 0.55, BLUE,  [
  { text: "Blue card — ", options: { fontSize: 9, fontFace: FONT, color: BLUE, bold: true } },
  { text: "default for most content", options: { fontSize: 9, fontFace: FONT, color: TEXT_DIM } },
]);
addCard(sCards, 0.4, 1.85, 4.3, 0.55, BLUE_LIGHT, [
  { text: "Light blue card — ", options: { fontSize: 9, fontFace: FONT, color: BLUE_LIGHT, bold: true } },
  { text: "secondary informational content", options: { fontSize: 9, fontFace: FONT, color: TEXT_DIM } },
]);
addCard(sCards, 0.4, 2.5, 4.3, 0.55, AMBER, [
  { text: "Amber card — ", options: { fontSize: 9, fontFace: FONT, color: AMBER, bold: true } },
  { text: "variety accent on white backgrounds", options: { fontSize: 9, fontFace: FONT, color: TEXT_DIM } },
]);
// Before/After pattern — the ONLY case for RED/GREEN accents
addCard(sCards, 0.4, 3.3, 4.3, 0.55, RED,   [
  { text: "Before — ", options: { fontSize: 9, fontFace: FONT, color: RED, bold: true } },
  { text: "bad code or wrong approach", options: { fontSize: 9, fontFace: FONT, color: TEXT_DIM } },
]);
addCard(sCards, 5.1, 3.3, 4.3, 0.55, GREEN, [
  { text: "After — ", options: { fontSize: 9, fontFace: FONT, color: GREEN, bold: true } },
  { text: "good code or correct approach", options: { fontSize: 9, fontFace: FONT, color: TEXT_DIM } },
]);

// ── EXAMPLE: Terminal with animated lines ──
const sTerm = addWhiteSlide("Terminal Example");
addTerminal(sTerm, 0.4, 1.15, 9.2, 3.5, "claude — ~/project", [
  [{ text: "$ ", color: TERM_GREEN }, { text: "claude", color: TERM_TEXT, bold: true }],
  [{ text: "⠋ ", color: TERM_BLUE }, { text: "Initializing...", color: TERM_DIM }],
  [{ text: "✓ ", color: TERM_GREEN }, { text: "Authenticated as ", color: TERM_TEXT }, { text: "dev@s2grupo.es", color: TERM_CYAN }],
  [{ text: "✓ ", color: TERM_GREEN }, { text: "Project: ", color: TERM_TEXT }, { text: "my-project", color: TERM_PURPLE }],
  "",
  [{ text: "Ready. How can I help?", color: TERM_DIM }],
  [{ text: "❯ ", color: TERM_GREEN }, { text: "█", color: TERM_GREEN }],
]);

// ── EXAMPLE: Table slide ──
const sTable = addWhiteSlide("Table Example");
const exampleTable = [
  [
    { text: "Column A", options: { bold: true, color: WHITE, fill: { color: BLUE }, fontSize: 9, fontFace: FONT } },
    { text: "Column B", options: { bold: true, color: WHITE, fill: { color: BLUE }, fontSize: 9, fontFace: FONT } },
    { text: "Column C", options: { bold: true, color: WHITE, fill: { color: BLUE }, fontSize: 9, fontFace: FONT } },
  ],
  [{ text: "Row 1A", options: { fontSize: 9 } }, { text: "Row 1B", options: { fontSize: 9 } }, { text: "Row 1C", options: { fontSize: 9 } }],
  [{ text: "Row 2A", options: { fontSize: 9 } }, { text: "Row 2B", options: { fontSize: 9 } }, { text: "Row 2C", options: { fontSize: 9 } }],
];
sTable.addTable(exampleTable, {
  x: 0.4, y: 1.3, w: 9.2,
  colW: [3.0, 3.1, 3.1],
  border: { pt: 0.5, color: "D0D5E0" },
  autoPage: false
});

// ── EXAMPLE: Closing slide ──
const sF = addBlueSlide();
sF.addText("¿Tienes\nalguna duda?", {
  x: 0.45, y: 1.2, w: 6, h: 2.2,
  fontSize: 40, fontFace: FONT, color: WHITE,
  bold: true, margin: 0
});
sF.addText("enigma.dev · S2Grupo", {
  x: 0.45, y: 4.4, w: 3, h: 0.3,
  fontSize: 11, fontFace: FONT, color: WHITE,
  transparency: 40, margin: 0
});


// ═══════════════════════════════════════════════
// SAVE — do not modify
// ═══════════════════════════════════════════════
const OUTPUT = process.env.OUTPUT || "output.pptx";
const META = process.env.META || "anim_meta.json";

pres.writeFile({ fileName: OUTPUT }).then(() => {
  console.log(`✓ Saved ${OUTPUT} (${pres.slides.length} slides)`);
  fs.writeFileSync(META, JSON.stringify(terminalAnimations, null, 2));
  console.log(`✓ Animation metadata → ${META}`);
});
