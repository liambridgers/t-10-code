package centerstage.auto;

import centerstage.Constants;

import com.acmerobotics.dashboard.config.Config;
import com.pocolifo.robobase.bootstrap.AutonomousOpMode;
import com.pocolifo.robobase.bootstrap.Hardware;
import com.pocolifo.robobase.motor.NovelMotor;
import com.pocolifo.robobase.motor.OmniDriveCoefficients;
import com.pocolifo.robobase.novel.NovelMecanumDrive;
import com.pocolifo.robobase.reconstructor.PathFinder;
import com.pocolifo.robobase.vision.Webcam;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Config
@Autonomous(name = "Pathfinder")
public class PathfinderTest extends AutonomousOpMode {
    @Hardware(name = "Webcam")
    public Webcam webcam;

//    private CarWheels carWheels;

    @Hardware(name = "FL", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor fl;

    @Hardware(name = "FR", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor fr;

    @Hardware(name = "BL", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor bl;

    @Hardware(name = "BR", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor br;

    private NovelMecanumDrive driver;

    private static final double MAX_SPEED = 8.0;


    @Override
    public void initialize() {
        this.driver = new NovelMecanumDrive(fl, fr, bl, br, new OmniDriveCoefficients(new double[]{ -1, -1, -1, 1 }));
    }

    @Override
    public void run() {
        try {
            PathFinder pathFinder = new PathFinder("points.txt");
            PathFinder.Point start = new PathFinder.Point(-60, -57);
            List<PathFinder.Point> points = pathFinder.findPath(start, new PathFinder.Point(60, -57));
            points.addAll(pathFinder.findPath(new PathFinder.Point(60, -57), new PathFinder.Point(60, 57)));
            points.addAll(pathFinder.findPath(new PathFinder.Point(60, 57), new PathFinder.Point(-60, 57)));
            PathFinder.Point currentPos = start;
            System.out.println("eee");
            for (PathFinder.Point point : points) {
                System.out.println(point);
            }
            System.out.println("eee");
            for (PathFinder.Point point : points) {
                int vertical = point.getY() - currentPos.getY();
                int horizontal = point.getX() - currentPos.getX();
                Movement movement = getMovement(vertical, horizontal);
                System.out.println("moving x: " + movement.getXSpeed() + " by y: " + movement.getYSpeed() + "\nfor: " + ((long)(movement.getTime() * 1000)) + " ms");
                driver.setVelocity(new Vector3D(movement.getYSpeed(), movement.getXSpeed(), 0));
                sleep((long)(movement.getTime() * 1000));
                currentPos = point;
            }
            driver.setVelocity(new Vector3D(0, 0, 0));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param xDist the horizontal distance to move in inches (can be negative)
     * @param yDist the vertical distance to move in inches (can be negative)
     * @return a movement object containing the speeds and time
     */
    private Movement getMovement(double xDist, double yDist) {
        double maxTime = Math.max(Math.abs(xDist), Math.abs(yDist)) / MAX_SPEED;
        double xSpeed = xDist / maxTime;
        double ySpeed = yDist / maxTime;
        return new Movement(xSpeed, ySpeed, maxTime);
    }
}

class Movement {
    private double xSpeed;
    private double ySpeed;
    private double time;

    public Movement(double xSpeed, double ySpeed, double time) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.time = time;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public double getTime() {
        return time;
    }
}