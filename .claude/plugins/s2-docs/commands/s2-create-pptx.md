---
name: s2-create-pptx
description: Creates a professional PPTX presentation with S2GRUPO branding, animated terminals, and corporate visual components
model: opus
---

# Command: /s2-create-pptx

Creates a professional presentation with S2GRUPO branding using the Node.js + Python pipeline.

## Usage

```bash
/s2-create-pptx
```

## Process

1. **Gather information** — Ask the user:
   - Presentation topic
   - Target audience (technical, commercial, executive)
   - Approximate number of slides
   - Desired structure (main sections/blocks)
   - (Logos are bundled in the skill — no need to ask the user)

2. **Read the skill** — Consult the `s2-docs:pptx` skill to understand:
   - The design system ([references/design-system.md](../skills/pptx/references/design-system.md))
   - Available components (slides, terminals, cards, tables)
   - The base template ([templates/build_presentation.js](../skills/pptx/templates/build_presentation.js))

3. **Generate the build script** — Create a `build.js` file in the user's working directory:
   - Copy logos to working directory: `cp ${CLAUDE_PLUGIN_ROOT}/skills/pptx/assets/*.b64 ./`
   - Copy the helper functions from the template (addBlueSlide, addWhiteSlide, addSectionSlide, addTerminal, addCard, branding constants, animation tracking and SAVE block)
   - Replace the "YOUR SLIDES START HERE" section with content adapted to the user's topic
   - Use appropriate components based on content type:
     - **Terminals** for CLI demos, code, commands
     - **Cards** for comparisons, feature lists, pros/cons
     - **Tables** for structured data, comparisons
     - **Section slides** for thematic block dividers
   - Ensure the first slide is a title (addBlueSlide) and the last is a closing slide

4. **Install dependencies** — Verify that `pptxgenjs` is available:
   ```bash
   npm list pptxgenjs 2>/dev/null || npm install pptxgenjs
   ```

5. **Run the build** — Generate the presentation:
   ```bash
   node build.js
   ```
   This generates `output.pptx` and `anim_meta.json`.

6. **Inject animations** — If there are terminals with animations:
   ```bash
   pip install python-pptx lxml 2>/dev/null
   python ${CLAUDE_PLUGIN_ROOT}/skills/pptx/scripts/inject_animations.py \
     --input output.pptx \
     --meta anim_meta.json \
     --output presentation-animated.pptx
   ```

7. **Visual QA** — Convert to images for review (if LibreOffice is available):
   ```bash
   soffice --headless --convert-to pdf presentation-animated.pptx 2>/dev/null && \
   pdftoppm -jpeg -r 150 presentation-animated.pdf slide
   ```
   If images were generated, show them to the user for review. If LibreOffice is not available, inform the user to open the file manually.

8. **Deliver** — Confirm the location of the final file and ask if they want adjustments.

## Rules

- ALWAYS use the color constants from the design system (BLUE, WHITE, TERM_*, etc.). NEVER invent colors outside the palette.
- ALWAYS include branding on all slides: logo, claim "Anticipando un mundo ciberseguro", footer with copyright.
- The helper functions (addBlueSlide, addWhiteSlide, etc.) already include branding. Do not duplicate it.
- Use Arial for general text and Consolas for code/terminal.
- Layout is 16:9 (10" x 5.625"). Do not exceed the grid margins.
- Each terminal line must be a segment with color. Use the TERM_* colors from the design system.
- Do not put more than 12-14 lines per terminal (limited vertical space).

## Example

```bash
/s2-create-pptx
```

```
What presentation do you want to create?
> Workshop on Claude Code for the development team

What is the target audience?
> Senior Java developers

How many slides approximately?
> 15-20

What main blocks do you want to cover?
> 1. What is Claude Code, 2. Setup, 3. Demo with real project, 4. Tips and best practices

Generating presentation...
✅ build.js created with 18 slides
✅ output.pptx generated (18 slides)
✅ presentation-animated.pptx with 24 injected animations

The final file is at: ./presentation-animated.pptx
Do you want to adjust anything?
```

## References

- Skill: `s2-docs:pptx`
