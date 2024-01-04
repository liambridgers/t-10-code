package centerstage.auto;

import centerstage.Constants;
import centerstage.SpikePosition;
import com.pocolifo.robobase.Alliance;
import com.pocolifo.robobase.StartSide;
import com.pocolifo.robobase.bootstrap.AutonomousOpMode;
import com.pocolifo.robobase.bootstrap.Hardware;
import com.pocolifo.robobase.motor.NovelMotor;
import com.pocolifo.robobase.motor.OmniDriveCoefficients;
import com.pocolifo.robobase.novel.NovelMecanumDrive;
import com.pocolifo.robobase.vision.NovelYCrCbDetection;
import com.pocolifo.robobase.vision.Webcam;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class BaseProductionAuto extends AutonomousOpMode {
    @Hardware(name = "Webcam")
    public Webcam webcam;

    @Hardware(name = "FL", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor fl;

    @Hardware(name = "FR", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor fr;

    @Hardware(name = "BL", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor bl;

    @Hardware(name = "BR", wheelDiameterIn = 3.7795275590551185, ticksPerRevolution = Constants.MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor br;

    @Hardware(name = "SpikeClaw")
    public CRServo spikeDropper;

    private final NovelYCrCbDetection spikeDetector;
    private final Alliance alliance;
    private final StartSide startSide;
    private NovelMecanumDrive driver;

    public BaseProductionAuto(NovelYCrCbDetection spikeDetector, Alliance alliance, StartSide startSide) {
        this.spikeDetector = spikeDetector;
        this.alliance = alliance;
        this.startSide = startSide;
    }

    @Override
    public void initialize() {
        this.webcam.open(this.spikeDetector);
        this.driver = new NovelMecanumDrive(this.fl, this.fr, this.bl, this.br, new OmniDriveCoefficients(new double[]{1, 1, -1, 1}));
    }

    @Override
    public void run() {
        NovelYCrCbDetection pipeline = (NovelYCrCbDetection) this.webcam.getPipeline();
        SpikePosition spikePosition = pipeline.getResult();

        switch (spikePosition) {
            case LEFT:
                driveVertical(-24, 2);
                sleep(500);
                driveHorizontal(-16, 1);
                break;

            case RIGHT:
                driveVertical(-24, 2);
                sleep(500);
                driveHorizontal(16, 1);
                break;

            case CENTER:
                driveVertical(-33, 3);
                break;
        }

        spikeDropper.setPower(-1);
        sleep(1000);
        spikeDropper.setPower(1);
        sleep(1000);
        spikeDropper.setPower(-1);

    }

    public void driveVertical(double inches, double time) {
        this.driver.setVelocity(new Vector3D(inches / time, 0, 0));

        sleep((long) (time * 1000L));

        this.driver.stop();
    }

    public void driveHorizontal(double inches, double time) {
        this.driver.setVelocity(new Vector3D(0, inches / time, 0));

        sleep((long) (time * 1000L));

        this.driver.stop();
    }
}
