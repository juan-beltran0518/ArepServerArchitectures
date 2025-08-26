package com.edu.esuelaing.arep;

@FunctionalInterface
public interface RouteHandler {
    String handle(Request request);
}
