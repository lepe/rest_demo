package com.alepe.rest_demo;

import com.alepe.rest_demo.auth.AuthService;
import com.alepe.rest_demo.person.Person;
import com.alepe.rest_demo.person.PersonAPI;
import com.intellisrc.core.SysInfo;
import com.intellisrc.core.SysService;
import com.intellisrc.db.Database;
import com.intellisrc.web.WebService;
import groovy.transform.CompileStatic;
import spark.Service;

/**
 * This class is the main controller of the system.
 */
@CompileStatic
public class Main extends SysService {
    public static final int port = 7777;
    public static boolean running = false;
    /*
     * Static initialization. By Using SysService, we are
     * converting this class into a Service which will run
     * in the background. It can be started/stopped/restarted
     * as any system service (it uses a lock file).
     *
     * Methods can be created (like onConsole), which translate into an argument, e.g:
     * `./run console`.
     *
     * Its a more elegant way than using `public static void main(String[] args)`.
     * As it becomes an instance class it is easier to test if required.
     */
    static {
        service = new Main();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite Driver is not present");
        }
    }

    final WebService ws = new WebService();
    /**
     * All starts here. This method is called on start.
     * We prepare database and web services.
     */
    @Override
    public void onStart() {
        /*
        The database uses a connection pool to recycle connections which
        improves performance. That pool will increase or decrease
        as needed.

        The next line will initialize the database object. Every time
        we call `DB db = Database.connect()` it will recycle a connection from
        the pool and `db.close()` will return it to the pool.
         */
        Database.init();
        // Create table if required:
        Person.initDB();

        /*
        The web server is running over Spark Framework. This implementation
        allow the creation of services in an organized way. We can add
        as many services as we need.

        Web Resources are kept in the user directory: resources/
         */
        ws.port = port;
        ws.setResources(SysInfo.getFile("resources","public"));
        ws.addService(new AuthService());
        ws.addService(new PersonAPI());
        ws.onStart = (Service service) -> running = true;
        ws.start(true);
    }

    /**
     * Stops web server and database
     */
    @Override
    public void onStop() {
        running = false;
        ws.stop();
        Database.quit();
    }

    /**
     * This method will launch a console
     * that will be used to display data
     * in the database.
     */
    @SuppressWarnings("unused")
    public void onConsole() {

    }
}
