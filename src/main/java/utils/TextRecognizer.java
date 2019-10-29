package utils;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.tesseract;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import static org.bytedeco.javacpp.lept.*;

public class TextRecognizer {
    private BytePointer outText;
    private tesseract.TessBaseAPI api;
    private static String source = "src\\main\\assets\\teseract\\";

    public void init() {
        api = new tesseract.TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path

        if (api.Init("src\\main\\assets\\tessdata", "eng") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

    }

    public void recognize(String filePath) {
        // Open input image with leptonica library
        //PIX image = pixRead(args.length > 0 ? args[0] : "/usr/src/tesseract/testing/phototest.tif");

        File imgPath = new File(source + filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage img;

        try {
            img = ImageIO.read(imgPath);
            ImageIO.write(img, "png", baos);
        } catch (IOException e) {
            System.err.println("Reading file or writing byte[] failed.");
            e.printStackTrace();
        }

        byte[] imageInByte = baos.toByteArray();

        //PIX image = pixReadMemPng(imageInByte, imageInByte.length);
        PIX image = pixRead(source+filePath);
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        System.out.println("OCR output:\n" + outText.getString());

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);
    }

    public static void main(String[] args) {
        TextRecognizer tr = new TextRecognizer();
        tr.init();
        tr.recognize("testing\\test1.png");
    }
}
