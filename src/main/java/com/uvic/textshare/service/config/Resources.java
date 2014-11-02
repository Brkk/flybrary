package com.uvic.textshare.service.config;

import com.uvic.textshare.service.rest.TextbookResource;
import com.uvic.textshare.service.rest.MailHandlerServlet;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class Resources extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<Class<?>>();
    s.add(TextbookResource.class);
    return s;
  }
}
