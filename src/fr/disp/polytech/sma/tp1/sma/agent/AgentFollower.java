package fr.disp.polytech.sma.tp1.sma.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector2d;

import fr.disp.polytech.sma.tp1.sma.agent.behaviour.BehaviourOutput;
import fr.disp.polytech.sma.tp1.sma.agent.behaviour.steering.SteeringBehaviourOutput;
import fr.disp.polytech.sma.tp1.sma.environment.AnimatBody;
import fr.disp.polytech.sma.tp1.sma.environment.AnimatPerception;
import fr.disp.polytech.sma.tp1.sma.environment.objet.PerceptionType;

@SuppressWarnings("restriction")

/**
 * Classe Visitor
 */
public class AgentFollower extends Animat {

    private static final double repulseObstacle = 0.05;
    private static final double repulseBorder = 2;
    private static final int radius = 50;
    int angle, distance;

    /**
     *
     * @param distance distance vis a vis du leader
     * @param angle vis a vis du leader (0 = juste derrière)
     */
    public AgentFollower(int distance, int angle) {
        super();
        this.distance = distance;
        this.angle = angle;
    }

    @Override
    /**
     * Méthode Start()
     */
    public void start() {
        this.getAgentBody().setType(PerceptionType.FOLLOWER);
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
            double influence_angle = 0;
            Vector2d influence = new Vector2d(0, 0);
            BehaviourOutput output = new SteeringBehaviourOutput();
            List<AnimatPerception> viewPercepts = getPerceptionFilter().getPerceivedObjects();

            ArrayList<Vector2d> agentLeaderPercept = new ArrayList<>();
            ArrayList<Vector2d> obstaclePercept = new ArrayList<>();

            // PERCEPTIONS
            if (getPerceptionFilter().hasPerceivedObjects()) {
                for (AnimatPerception viewPercept : viewPercepts) {
                    switch (viewPercept.getType()) {
                        case OBSTACLE:
                            obstaclePercept.add(viewPercept.getData());
                            break;
                        case LEADER:
                            agentLeaderPercept.add(viewPercept.getData());
                            break;
                    }
                }
            }

            // BEWARE OF BORDERS
            if (obstaclePercept.size() > 0) {
                Vector2d r = repulsion(obstaclePercept);
                r.scale(repulseBorder);
                influence.add(r);
            }

            // FOLLOW THE LEADER
            if (agentLeaderPercept.size() > 0) {
                // Choose one
                Vector2d leader = agentLeaderPercept.get(0);
                // Set target point (thanks thales)
                double distanceToLeader = Math.sqrt(Math.pow(leader.x - body.getX(), 2) + Math.pow(leader.y - body.getY(), 2));
                Vector2d target = new Vector2d(((distance * (body.getX() - leader.x)) / distanceToLeader) + leader.x, ((distance * (body.getY() - leader.y)) / distanceToLeader) + leader.y);
                
                // Fk = k(l-l0)*v
                Vector2d force = new Vector2d(target.x - body.getX(), target.y - body.getY());
                force.normalize();
                double forceValue = Math.sqrt(Math.pow(target.x - body.getX(), 2) + Math.pow(target.y - body.getY(), 2));

                force.x *= forceValue * 0.1;
                force.y *= forceValue * 0.1;
                influence.add(force);
            } else {
                // DO RANDOM THINGS
                influence_angle = (Math.random() - 0.5) * 0.2 + Math.toRadians(body.getOrientation());
                influence.add(new Vector2d(Math.cos(influence_angle), Math.sin(influence_angle)));
            }
            
            ((SteeringBehaviourOutput) output).setLinearAcceleration(influence.x, influence.y);
            ((SteeringBehaviourOutput) output).setAngularAcceleration(Math.toDegrees(Math.atan2(output.getLinear().y, output.getLinear().x)) - body.getOrientation());

            body.influenceSteering(output.getLinear(), output.getAngular());

        }

    }

    private Vector2d repulsion(ArrayList<Vector2d> obstaclePercept) {
        Vector2d p = new Vector2d();
        Vector2d tmp = new Vector2d();

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
        Vector2d tmp = new Vector2d();

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
