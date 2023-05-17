package endpoints;

import tools.CreateRequest;
import tools.QRReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/encode")
public class EncodingResource {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Produces("image/png")
    public Response createQR(CreateRequest request){
        int httpStatus = 200;

        byte[] byteResponse = null;

        try {
            byteResponse = new QRReader().createQR(request.data, request.size);
        } catch (Throwable e){
            httpStatus = 500;
        }

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(byteResponse);
        return builder.build();
    }
}
