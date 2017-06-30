package com.fourplay.business.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is used to create images with the given text or write text into an existing image.
 * 30/06/2017
 *
 * @author Ahmet Cetin
 */
@Component
public class TextToGraphics {
    Logger logger = LogManager.getLogger(TextToGraphics.class.getName());

    private Integer maxFontSize = 54;
    private Integer minFontSize = 12;

    private Integer imageWidth = 500;
    private Integer imageHeight = 200;

    public void convertTextToImage(String text, String sourcePath, String destionationPath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(sourcePath));
        } catch (IOException e) {
            logger.error("Error while reading the file from sourcePath: " + sourcePath, e);
        }

        if (img == null) {
            logger.error("Could not find the image in this path:" + sourcePath);
            return;
        }

        Graphics2D g2d = img.createGraphics();
        String fontName = "Arial";
        Integer fontSize = maxFontSize;
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        imageWidth = img.getWidth();

        FontMetrics fm = g2d.getFontMetrics();
        Integer lineHeight = fm.getHeight();

        /*
         * After giving the font size, FontMetrics gives us the width and height.
         * If the width and heigth are not suitable, we can change it with a new font size.
         */

        String[] words = text.split(" ");
        String line = "";
        List<String> lines = new ArrayList<>();

        boolean needsOperation = true;
        boolean converted = true;

        while (needsOperation) {

            font = new Font(fontName, Font.PLAIN, fontSize);
            g2d.setFont(font);

            fm = g2d.getFontMetrics();
            lineHeight = fm.getHeight();

            for (String word : words) {
                if (fm.stringWidth(line + word) + 20 < imageWidth) {
                    line += word + " ";
                } else {
                    lines.add(line);
                    line = word + " ";
                }
            }
            if (!Objects.equals(line, "")) {
                lines.add(line);
            }

            if (lines.size() * (lineHeight) > imageHeight) {
                fontSize--;
                lines.clear();
                line = "";
            } else {
                needsOperation = false;
            }

            if (fontSize < minFontSize) {
                logger.error("Not suitable size");
                converted = false;
                needsOperation = false;
            }
        }

        if (converted) {
            try {
                img = ImageIO.read(new File(sourcePath));
                g2d = img.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2d.setFont(font);

                g2d.setColor(Color.decode("#4E4E4E"));

                Integer startPixel = 470;
                for (String line1 : lines) {
                    g2d.drawString(line1, 20, startPixel);
                    startPixel += lineHeight + 5;
                }

                g2d.dispose();

                ImageIO.write(img, "png", new File(destionationPath));
            } catch (IOException e) {
                logger.error("Could not write the created file to destination path: " + destionationPath, e);
            }
        }
    }
}
