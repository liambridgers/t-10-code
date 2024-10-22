package t10.reconstructor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.utils.MovementVector;

/**
 * A 2D Point with a direction.
 */
public class Pose extends Point {
    protected double headingRadians;

    public Pose(double x, double y, double heading, AngleUnit angleUnit) {
        super(x, y);
        this.headingRadians = angleUnit.toRadians(heading);
    }

    /**
     * IMPORTANT: Novel uses INCHES for x, y, and ROTATION. Therefore, this assumes the Z coordinate of the vector is in inches.
     * @param novelVector A vector that you would pass into a Novel setVelocity call.
     */
    public Pose(MovementVector novelVector, double robotDiameter) {
        super(novelVector.getHorizontal(), novelVector.getVertical());

        /*
         * Proof of headingRadians calculation.
         *
         * 1.
         * Robot circumference = PI * robotDiameter
         * > Robot circumference is the number of inches of a full rotation by definition.
         *
         * 2.
         * Fraction of circumference rotated = (novelVector.getZ()) / (Robot circumference)
         * > Fraction of circumference rotated is also the fraction of a full rotation.
         *
         * 3.
         * Heading in radians = (Fraction of circumference rotated) * (2 * PI)
         * > Recall that a full rotation is 2 * PI radians. Therefore, multiplying the fraction
         * > of circumference rotated will end with the number of radians rotated.
         *
         * Simplification is what headingRadians is set to below..
         */
        this.headingRadians = (2 * novelVector.getRotation()) / (robotDiameter);
    }

    public void setHeading(double heading, AngleUnit angleUnit) {
        this.headingRadians = angleUnit.toRadians(heading);
    }

    public double getHeading(AngleUnit angleUnit) {
        return angleUnit.fromRadians(this.headingRadians);
    }

    public Pose add(Pose pose) {
        return new Pose(
                this.x + pose.x,
                this.y + pose.y,
                this.headingRadians + pose.headingRadians,
                AngleUnit.RADIANS
        );
    }

    public Pose subtract(Pose pose) {
        return new Pose(
                this.x - pose.x,
                this.y - pose.y,
                this.headingRadians - pose.headingRadians,
                AngleUnit.RADIANS
        );
    }

    public static MovementVector toMovementVector(Pose pose) {
        return new MovementVector(pose.getX(), pose.getY(), pose.getHeading(AngleUnit.DEGREES));
    }

    public static Pose fromMovementVector(MovementVector movementVector) {
        return new Pose(movementVector.getHorizontal(), movementVector.getVertical(), movementVector.getRotation(), AngleUnit.DEGREES);
    }
}
