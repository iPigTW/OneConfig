package cc.polyfrost.oneconfig.gui.elements.text;

import cc.polyfrost.oneconfig.config.OneConfigConfig;
import cc.polyfrost.oneconfig.gui.elements.BasicElement;
import cc.polyfrost.oneconfig.lwjgl.RenderManager;
import cc.polyfrost.oneconfig.lwjgl.image.Images;
import cc.polyfrost.oneconfig.utils.ColorUtils;
import org.lwjgl.nanovg.NanoVG;

public class NumberInputField extends TextInputField {
    private final BasicElement upArrow = new BasicElement(12, 14, false);
    private final BasicElement downArrow = new BasicElement(12, 14, false);
    private float min;
    private float max;
    private float step;
    private int colorTop, colorBottom;
    private float current;

    public NumberInputField(int width, int height, float defaultValue, float min, float max, float step) {
        super(width - 16, height, true, "");
        super.onlyNums = true;
        this.min = min;
        this.max = max;
        this.step = step;
        this.input = String.format("%.01f", defaultValue);
    }

    @Override
    public void draw(long vg, int x, int y) {
        super.errored = false;
        RenderManager.drawRoundedRect(vg, x + width + 4, y, 12, 28, OneConfigConfig.GRAY_500, 6f);
        upArrow.update(x + width + 4, y);
        downArrow.update(x + width + 4, y + 14);
        try {
            current = Float.parseFloat(input);
        } catch (NumberFormatException e) {
            super.errored = true;
        }

        if (current < min || current > max) {
            super.errored = true;
        } else {
            upArrow.disable(false);
            downArrow.disable(false);
        }
        if (current == max) colorTop = OneConfigConfig.GRAY_500_80;
        if (current == min) colorBottom = OneConfigConfig.GRAY_500_80;

        colorTop = ColorUtils.getColor(colorTop, 2, upArrow.isHovered(), upArrow.isClicked());
        colorBottom = ColorUtils.getColor(colorBottom, 2, downArrow.isHovered(), downArrow.isClicked());
        if (upArrow.isClicked()) {
            current += step;
            if (current > max) current = max;
            setCurrentValue(current);
        }
        if (downArrow.isClicked()) {
            current -= step;
            if (current < min) current = min;
            setCurrentValue(current);
        }
        if (current >= max) {
            NanoVG.nvgGlobalAlpha(vg, 0.3f);
            upArrow.disable(true);
        }
        RenderManager.drawRoundedRectVaried(vg, x + width + 4, y, 12, 14, colorTop, 6f, 6f, 0f, 0f);
        RenderManager.drawImage(vg, Images.UP_ARROW, x + width + 5, y + 2, 10, 10);
        if (current >= max) NanoVG.nvgGlobalAlpha(vg, 1f);

        if (current <= min) {
            NanoVG.nvgGlobalAlpha(vg, 0.3f);
            downArrow.disable(true);
        }
        RenderManager.drawRoundedRectVaried(vg, x + width + 4, y + 14, 12, 14, colorBottom, 0f, 0f, 6f, 6f);
        NanoVG.nvgTranslate(vg, x + width + 15, y + 25);
        NanoVG.nvgRotate(vg, (float) Math.toRadians(180));
        RenderManager.drawImage(vg, Images.UP_ARROW, 0, 0, 10, 10);
        NanoVG.nvgResetTransform(vg);
        NanoVG.nvgGlobalAlpha(vg, 1f);

        try {
            super.draw(vg, x, y - 2);
        } catch (Exception e) {
            setCurrentValue(current);
            super.caretPos = 0;
            super.prevCaret = 0;
        }
    }


    public float getCurrentValue() {
        return current;
    }

    public void setCurrentValue(float value) {
        input = String.format("%.01f", value);
    }

    @Override
    public void onClose() {
        try {
            if (current < min) current = min;
            if (current > max) current = max;
            setCurrentValue(current);
        } catch (Exception ignored) {

        }
    }

    public void setStep(float step) {
        this.step = step;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public boolean arrowsClicked() {
        return upArrow.isClicked() || downArrow.isClicked();
    }
}