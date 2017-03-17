package fr.disp.polytech.sma.tp1.sma.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Message;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

import fr.disp.polytech.sma.tp1.sma.agent.behaviour.BehaviourOutput;
import fr.disp.polytech.sma.tp1.sma.agent.behaviour.steering.SteeringBehaviourOutput;
import fr.disp.polytech.sma.tp1.sma.environment.AnimatBody;
import fr.disp.polytech.sma.tp1.sma.environment.AnimatPerception;
import fr.disp.polytech.sma.tp1.sma.environment.AnimatViewPerception;
import fr.disp.polytech.sma.tp1.sma.environment.Info;
import fr.disp.polytech.sma.tp1.sma.environment.objet.PerceptionType;

@SuppressWarnings("restriction")

/**
 * Classe Visitor
 */
public class StandardAgent extends Animat {

    protected static double repulseNegative = .005;
    protected static double repulseObstacle = 0.7;
    protected static double repulseLimitObstacle = 0.0005;
    protected static double attractGoal = .5;
    protected static int radius = 40;
    protected static double maxForce = 100;

    /**
     * @param formationPosition
     */
    public StandardAgent() {
        super();
    }

    @Override
    /**
     * Méthode Start()
     */
    public void start() {

        this.getAgentBody().setType(PerceptionType.STANDARDAGENT);
        this.getAgentBody().getViewFustrum().setRadius(radius);
    }

    @Override
    /**
     * Méthode DoDecisionAndAction
     */
    protected void doDecisionAndAction() {
        // compute static parameters
        AnimatBody body = getAgentBody();
        if (body.isFreezed()) {
            this.killMe();
        } else {
            Vector2d influence;

            List<AnimatPerception> viewPercepts = getPerceptionFilter().getPerceivedObjects();

            BehaviourOutput output = new SteeringBehaviourOutput();

            Vector2d force = new Vector2d();

            if (getPerceptionFilter().hasPerceivedObjects()) {

                ArrayList<Vector2d> agentPercept = new ArrayList<Vector2d>();
                ArrayList<Vector2d> obstaclePercept = new ArrayList<Vector2d>();

                // TRAITEMENT DES PERCEPTIONS
                for (AnimatPerception viewPercept : viewPercepts) {
                    switch (viewPercept.getType()) {
                        case OBSTACLE:
                            obstaclePercept.add(viewPercept.getData());
                            break;
                        case STANDARDAGENT:
                            agentPercept.add(viewPercept.getData());
                            break;

                    }
                }

                if (obstaclePercept.size() > 0) {
                    influence = repulsion(obstaclePercept);
                    
                    if (influence.length() > this.maxForce) {
                        influence.normalize();
                        influence.scale(this.maxForce);
                    }

                    ((SteeringBehaviourOutput) output).setLinearAcceleration(influence.x, influence.y);

                    if (output != null) {
                        body.influenceSteering(output.getLinear(), output.getAngular());
                    }
                }
            }
        }
    }

    private Vector2d repulsion(ArrayList<Vector2d> obstaclePercept) {
        Vector2d p = new Vector2d();
        Vector2d tmp;

        for (Vector2d perception2d : obstaclePercept) {
            if (perception2d == null) {
                continue;
            }

            tmp = new Vector2d(this.getAgentBody().getX() - perception2d.x,
                    this.getAgentBody().getY() - perception2d.y);

            if (tmp.lengthSquared() == 0) {
                Random randomGenerator = new Random();
                tmp = new Vector2d(randomGenerator.nextInt(100), randomGenerator.nextInt(100));
                tmp.normalize();
                tmp.scale(10);
            } else {
                tmp.scale(1 / tmp.lengthSquared());
            }
            //System.out.println(tmp.length());
            p.add(tmp);
        }
        p.normalize();
        return p;
    }

    private Vector2d separation(ArrayList<Vector2d> agentPercept) {
        Vector2d p = new Vector2d();
        Vector2d tmp;

        for (Vector2d perception2d : agentPercept) {
            tmp = new Vector2d(this.getAgentBody().getX() - perception2d.x,
                    this.getAgentBody().getY() - perception2d.y);

            if (tmp.lengthSquared() == 0) {
                Random randomGenerator = new Random();
                tmp = new Vector2d(randomGenerator.nextInt(100), randomGenerator.nextInt(100));
                tmp.normalize();
                tmp.scale(10);
            } else {
                tmp.scale(1 / tmp.lengthSquared());
            }

            p.add(tmp);
        }

        p.normalize();

        return p;
    }

}
