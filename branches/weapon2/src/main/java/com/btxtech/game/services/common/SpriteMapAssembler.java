package com.btxtech.game.services.common;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * User: beat
 * Date: 24.09.13
 * Time: 13:27
 *
 * Only tested via using classes
 */
public class SpriteMapAssembler {
    private int xPos;
    private BufferedImage spriteMap;
    private String formatName;
    private String mimeType;
    private Graphics2D graphics2D;

    public SpriteMapAssembler(int imageCount, byte[] exampleImageData) throws IOException {
        // Get the format name
        Iterator<ImageReader> iter = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(exampleImageData)));
        if (!iter.hasNext()) {
            throw new IllegalArgumentException("Can not find image reader");
        }
        ImageReader imageReader = iter.next();
        formatName = imageReader.getFormatName();
        mimeType = imageReader.getOriginatingProvider().getMIMETypes()[0];
        BufferedImage masterImage = ImageIO.read(new ByteArrayInputStream(exampleImageData));
        spriteMap = new BufferedImage(masterImage.getWidth() * imageCount, masterImage.getHeight(), masterImage.getType());
        graphics2D = spriteMap.createGraphics();
    }

    public void appendImage(byte[] dbItemTypeImageData) throws IOException {
        if(dbItemTypeImageData == null || dbItemTypeImageData.length == 0) {
            return;
        }
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImageData));
        boolean done = graphics2D.drawImage(image, xPos, 0, null);
        if (!done) {
            throw new IllegalStateException("Image could not be drawn: ");
        }
        xPos += image.getWidth();
    }

    public byte[] assemble() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(spriteMap, formatName, outputStream);
        return outputStream.toByteArray();
    }

    public String getMimeType() {
        return mimeType;
    }
}
