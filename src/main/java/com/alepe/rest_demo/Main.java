package com.alepe.rest_demo;

import com.intellisrc.core.SysInfo;
import com.intellisrc.core.SysService;
import com.intellisrc.db.Database;
import com.intellisrc.web.WebService;
import groovy.transform.CompileStatic;

/**
 * Main class
 */
@CompileStatic
class Main extends SysService {
    static {
        service = new Main();
    }

    final WebService ws = new WebService();

    @Override
    public void onStart() {
        // Initialize Database:
        Database.init();

        // Initialize Web service:
        ws.port = 7777;
        ws.setResources(SysInfo.getFile("resources"));
        ws.addService(new PersonService());
        ws.start();
    }

    @Override
    public void onStop() {
        ws.stop();
    }
}
