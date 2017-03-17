package fr.disp.polytech.sma.tp1;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.MessageTransportService;
import org.arakhne.tinyMAS.core.YellowPages;
import org.jdom2.JDOMException;

import fr.disp.polytech.sma.tp1.gui.GUI;
import fr.disp.polytech.sma.tp1.sma.agent.AgentLeader;
import fr.disp.polytech.sma.tp1.sma.agent.AgentFollower;
import fr.disp.polytech.sma.tp1.sma.agent.Animat;
import fr.disp.polytech.sma.tp1.sma.agent.StandardAgent;
import fr.disp.polytech.sma.tp1.sma.environment.WorldModel;
import fr.disp.polytech.sma.tp1.sma.environment.objet.EnvironmentObject;
import fr.disp.polytech.sma.tp1.sma.environment.objet.StandardEntity;
import fr.disp.polytech.sma.tp1.util.ThreadUtil;

@SuppressWarnings("restriction")
public class Simulation extends
        Kernel<Animat, WorldModel, YellowPages, MessageTransportService> {

    public static double WORLD_SIZE_X = 700;
    public static double WORLD_SIZE_Y = 700;
    private WorldModel g;

    public void setWorld(WorldModel g) {
        this.g = g;
    }

    public WorldModel getWorldModel() {
        return g;
    }

    public Simulation() {
        super();

    }

    /**
     * Programme Principal
     *
     * @throws IOException
     * @throws JDOMException
     */
    public void init() {

        g = new WorldModel(WORLD_SIZE_X, WORLD_SIZE_Y);
        this.setEnvironment(g);

    }

    public void start() {

        for (int i = 0; i < 5; i++) {
            AgentLeader leader = new AgentLeader();
            this.addAgent(leader);

        }

        for (int i = 0; i < 10; i++) {
            AgentFollower f = new AgentFollower(30,0);
            this.addAgent(f);
        }

        for (int i = 0; i <= WORLD_SIZE_X; i += 10) {
            for (int j = 0; j <= WORLD_SIZE_Y; j +=10) {
                if(i == 0 || i == WORLD_SIZE_X || j== 0 || j == WORLD_SIZE_Y)
                this.g.addObject(new StandardEntity(new Point2d(i - WORLD_SIZE_X/2, j - WORLD_SIZE_Y/2), g, true));
            }
        }
        

        this.setWaitingDuration(10);

        this.run();
        // Force quit
        System.exit(0);
    }

}
