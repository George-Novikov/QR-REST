package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/status")
public class StatusController {
    @GET
    @Path("/ping")
    public Response ping(){
        Response.ResponseBuilder builder = Response.ok().entity("QR service is online.");
        return builder.build();
    }
}
