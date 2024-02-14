package controllers;

import constants.ServiceMessage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.QRService;
import tools.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Path("/decode")
public class DecodingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DecodingController.class);

    @Inject
    private QRService qrReader;

    @POST
    @Path("/pdf")
    @Consumes("application/pdf")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response readPDF(InputStream input){
        try {
            List<Object> data = new ArrayList();

            List<BufferedImage> bufferedImages = PDFConverter.pdfToImage(input);
            if (bufferedImages == null) return Responder.sendError(3, ServiceMessage.NULL_IMAGE_BUFFER);

            for (BufferedImage bufferedImage : bufferedImages){
                String result = qrReader.readQR(bufferedImage);
                if (result != null && !result.isEmpty()) {
                    data.add(result);
                }
            }

            return Responder.sendOk(data);
        } catch (IOException ioe){
            return Responder.sendError(1, ServiceMessage.PDF_CONVERSION_ERROR, ioe);
        } catch (NotFoundException nfe) {
            return nfe.getMessage() != null ? Responder.sendError(2, ServiceMessage.QR_ERROR, nfe) : Responder.sendError(2, ServiceMessage.EMPTY_SOURCE);
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return Responder.sendError(4, ServiceMessage.PDF_READ_ERROR, e);
        }
    }

    @POST
    @Path("/image")
    @Consumes({"image/jpg", "image/png", "image/gif", "image/svg+xml", "image/webp", "image/avif", "image/apng"})
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response readImage(InputStream input){
        try {
            List<Object> data = new ArrayList();

            BufferedInputStream bufferStream = new BufferedInputStream(input);
            BufferedImage bufferedImage = ImageIO.read(bufferStream);
            String result = qrReader.readQR(bufferedImage);

            if (result != null) data.add(result);

            return Responder.sendOk(data);
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return Responder.sendError(4, ServiceMessage.IMAGE_READ_ERROR, e);
        }
    }

    @POST
    @Path("/map")
    @Consumes("application/pdf")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response mapDocument(InputStream input){
        try {
            List<Object> data = PDFConverter.mapSubdocuments(input);
            return Responder.sendOk(data);
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return Responder.sendError(4, ServiceMessage.PDF_READ_ERROR, e);
        }
    }

    @POST
    @Path("/getsubdoc")
    @Consumes("application/pdf")
    @Produces("application/pdf")
    public Response getSubdocument(InputStream input,
                                   @QueryParam("start") int start,
                                   @QueryParam("end") int end){
        byte[] output = null;
        try {
            PDDocument subdocument = PDFConverter.getSubdocument(input, start, end);

            if (subdocument != null){
                output = PDFConverter.toByteArray(subdocument);
                subdocument.close();
            } else {
                output = PDFConverter.getEmptyPDFBytes();
            }

            return Responder.sendOk(output);
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}