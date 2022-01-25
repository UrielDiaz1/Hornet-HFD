package org.csc133.a5.views;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Component;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.geom.Dimension;
import java.io.IOException;

public class DigitComponent extends Component {
    private final Image[] componentDigits;
    private final Image[] digitImages;
    private final int numDigitsShowing;
    private Image colonImage;
    private int ledColor;
    private double value;

    public DigitComponent(int numDigitsShowing) {
        this.value            = 0;
        this.numDigitsShowing = numDigitsShowing;
        digitImages           = new Image[10];
        componentDigits       = new Image[numDigitsShowing];
        initImages();
        initComponentDigits();
    }

    private void initImages() {
        try {
            digitImages[0] = Image.createImage("/LED_digit_0.png");
            digitImages[1] = Image.createImage("/LED_digit_1.png");
            digitImages[2] = Image.createImage("/LED_digit_2.png");
            digitImages[3] = Image.createImage("/LED_digit_3.png");
            digitImages[4] = Image.createImage("/LED_digit_4.png");
            digitImages[5] = Image.createImage("/LED_digit_5.png");
            digitImages[6] = Image.createImage("/LED_digit_6.png");
            digitImages[7] = Image.createImage("/LED_digit_7.png");
            digitImages[8] = Image.createImage("/LED_digit_8.png");
            digitImages[9] = Image.createImage("/LED_digit_9.png");

            colonImage = Image.createImage("/LED_colon.png");
        } catch (IOException e) {e.printStackTrace();}
    }

    private void initComponentDigits() {
        for(int i = 0; i < numDigitsShowing; i++) {
            componentDigits[i] = digitImages[0];
        }
    }

    void setValue(int value) {
        this.value = value;
    }

    void setLedColor(int ledColor) {
        this.ledColor = ledColor;
    }

    private void start() {
        getComponentForm().registerAnimated(this);
    }

    private void stop() {
        getComponentForm().deregisterAnimated(this);
    }

    @Override
    public boolean animate() {
        update();
        return true;
    }

    private void update() {
        double tempValue = value;
        for(int i = numDigitsShowing - 1; i >= 0 ; i--) {
            componentDigits[i] = digitImages[(int) tempValue % 10];
            tempValue = tempValue / 10;
        }
    }

    @Override
    protected Dimension calcPreferredSize() {
        return new Dimension(colonImage.getWidth() * numDigitsShowing,
                colonImage.getHeight());
    }

    @Override
    public void laidOut() {
        this.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final int COLOR_PAD = 1;

        int digitWidth  = componentDigits[0].getWidth();
        int digitHeight = componentDigits[0].getHeight();
        int clockWidth  = numDigitsShowing * digitWidth;

        float scaleFactor = Math.min(getInnerHeight() / (float) digitHeight,
                                     getInnerWidth() / (float) clockWidth);

        int displayDigitWidth  = (int) (scaleFactor * digitWidth);
        int displayDigitHeight = (int) (scaleFactor * digitHeight);
        int displayClockWidth  = displayDigitWidth * numDigitsShowing;

        int displayX = getX() + (getWidth() - displayClockWidth) / 2;
        int displayY = getY() + (getHeight() - displayDigitHeight) / 2;

        g.setColor(ColorUtil.BLACK);
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        g.setColor(ledColor);
        g.fillRect(displayX + COLOR_PAD,
                   displayY + COLOR_PAD,
                   displayClockWidth  - COLOR_PAD * 2,
                   displayDigitHeight - COLOR_PAD * 2);

        for(int digit = 0; digit < numDigitsShowing; digit++) {
            g.drawImage(componentDigits[digit],
                        displayX + digit * displayDigitWidth,
                        displayY,
                        displayDigitWidth,
                        displayDigitHeight);
        }
    }
}