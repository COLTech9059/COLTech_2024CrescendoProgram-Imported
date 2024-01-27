package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.IO;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class Manipulator {
    
    //Create the motor controller objects
    CANSparkMax leftBaseMotor = new CANSparkMax(Constants.leftBaseID, MotorType.kBrushed);
    CANSparkMax rightBaseMotor = new CANSparkMax(Constants.rightBaseID, MotorType.kBrushed);
    CANSparkMax ampMotor = new CANSparkMax(Constants.ampID, MotorType.kBrushless);
    static CANSparkMax rightIntakeMotor = new CANSparkMax(Constants.rightIntakeID, MotorType.kBrushless);
    CANSparkMax leftIntakeMotor = new CANSparkMax(Constants.leftIntakeID, MotorType.kBrushless);

    //Create the encoder objects
    RelativeEncoder leftBaseEncoder = leftBaseMotor.getEncoder();
    RelativeEncoder rightBaseEncoder = rightBaseMotor.getEncoder();
    RelativeEncoder ampEncoder = ampMotor.getEncoder();
    RelativeEncoder rightIntakeEncoder = rightIntakeMotor.getEncoder();
    RelativeEncoder leftIntakeEncoder= leftIntakeMotor.getEncoder();

    //Create the digital input objects
    static DigitalInput beamSensor = new DigitalInput(Constants.beamSensorID);
    DigitalInput magneticSensor = new DigitalInput(Constants.magneticSensorID);

    private boolean usingDigitalSensors = true;

    //#INITIALIZEMANIPULATOR
    //This method will set up the manipulator for use
    public void initializeManipulator() {

        //Reset the digital sensors boolean
        usingDigitalSensors = true;

        //Reset intake boolean
        if (!beamSensor.get()) {didIntake = false;} else {didIntake = true;}

        //Reset the motors to their factory defaults
        leftBaseMotor.restoreFactoryDefaults();
        rightBaseMotor.restoreFactoryDefaults();
        ampMotor.restoreFactoryDefaults();
        rightIntakeMotor.restoreFactoryDefaults();
        leftIntakeMotor.restoreFactoryDefaults();

        //Set the leftIntakeMotor as a follower
        leftIntakeMotor.follow(rightIntakeMotor);
        leftIntakeMotor.setInverted(true);

        //Set the leftBaseMotor as a follower
        leftBaseMotor.follow(rightBaseMotor);
        leftBaseMotor.setInverted(true);

        //Set the encoders to 0, effectively resetting them
        leftBaseEncoder.setPosition(0);
        rightBaseEncoder.setPosition(0);
        ampEncoder.setPosition(0);
        rightIntakeEncoder.setPosition(0);
        leftIntakeEncoder.setPosition(0);
    }



    //#manualControl
    //This method toggles the use of digital sensors duting teleop
    public void manualControl() {
        if (usingDigitalSensors) {usingDigitalSensors = false;} else {usingDigitalSensors = true;}
    }

    
    private static boolean didAmpPosition = false;
    private static Timer posTimer = new Timer();
    //#AMPPOSITION
    //This method will run the manipulator base motors until the magnetic sensor is triggered at the amp spitting position
    public void ampPosition(double timeout) {
        posTimer.reset();
        posTimer.start();
        if (posTimer.get() <= timeout) {
        if (!magneticSensor.get()) {
            rightBaseMotor.set(-0.3);
        } else if (magneticSensor.get()) {
            rightBaseMotor.set(0);
            rightBaseEncoder.setPosition(0);
            leftBaseEncoder.setPosition(0);

            didAmpPosition = true;
        }
    } else {
        rightBaseMotor.set(0);
        didAmpPosition = false;
    }
    }


    private static Timer shootPosTime = new Timer();
    private boolean didShootPosition = false;
    //#SHOOTPOSITION
    //This method will bring the manipulator to a position for it to shoot from
    public void shootPosition(double timeout) {
        shootPosTime.reset();
        shootPosTime.start();
        if (shootPosTime.get() <= timeout) {
        if (rightBaseEncoder.getPosition() <= Constants.shootPosition) {
            rightBaseMotor.set(0.3);
        } else {
            rightBaseMotor.set(0);
            didShootPosition = true;
        }
    } else {
        rightBaseMotor.set(0);
        didShootPosition = false;
    }
    }


        //#MANIPULATORDASHBOARD
        //This method updates the dashboard with all the data from the manipulator class
        public void manipulatorDashboard() {
            //Push the digital sensor data to the shuffleboard
            SmartDashboard.putBoolean("Beam Sensor", beamSensor.get());
            SmartDashboard.putBoolean("Magnetic Sensor", magneticSensor.get());
        }



        public static boolean didIntake = false;
        private static Timer intakeTime = new Timer();
        //#INTAKE
        //This method will intake a note
        public static void intake(double timeout) {
            intakeTime.reset();
            intakeTime.start();
        if (intakeTime.get() <= timeout) {
            if (!beamSensor.get()) {
                rightIntakeMotor.set(0.4);
            } else {
                rightIntakeMotor.set(0);
                didIntake = true;
            }
        } else {
            rightIntakeMotor.set(0);
            didIntake = false;
        }
        }

        //#INTAKE
        //This method will manually intake a note
        public void intake() {
            if (IO.dController.getLeftTriggerAxis() > 0.4) {rightIntakeMotor.set(0.4);} else {rightIntakeMotor.set(0);} 
        }



        private Timer shootTime = new Timer();
        private static Timer shootTimeout = new Timer();

        //#SHOOTNOTE
        //This method will shoot a note
        public void shootNote(double timeout) {

            shootTimeout.reset();
            shootTimeout.start();
            shootTime.reset();

            if (shootTimeout.get() <= timeout) {
            //If the beam sensor is active, the intake motor runs in reverse until the beam sensor is deactivated,
            // at which point the intake motor will stop and the amp motor will run for 1 second at full power to shoot
            if (beamSensor.get()) {
                rightIntakeMotor.set(0.4);
            } else if (!beamSensor.get()) {
                shootTime.start();
                rightIntakeMotor.set(0);
                ampMotor.set(1);
            } 
            if (!beamSensor.get() && shootTime.get() >= 1) {
                ampMotor.set(0);
                shootTime.stop();
            }
        }
        }

        //#SHOOTNOTE
        //This method will shoot a note manually
        public void shootNote() {
            if (IO.dController.getRightTriggerAxis() > 0.4) {
                rightIntakeMotor.set(0.4);
                ampMotor.set(1);
            } else {
                rightIntakeMotor.set(0);
                ampMotor.set(0);
            }
        }



        private Timer ampTime = new Timer();
        private static Timer ampTimeout = new Timer();
        //#AMPSCORE
        //This method will score a note in the amp
        public void ampScore(double timeout) {

            ampTimeout.reset();
            ampTimeout.start();
            ampTime.reset();

            if (ampTimeout.get() <= timeout) {
            //If the beam sensor is active, the intake motor runs in reverse until the beam sensor is deactivated,
            // at which point the intake motor will stop and the amp motor will run for 1.5 seconds at 40% power to score
            if (beamSensor.get()) {
                rightIntakeMotor.set(0.4);
            } else if (!beamSensor.get()) {
                ampTime.start();
                rightIntakeMotor.set(0);
                ampMotor.set(0.3);
            } 
            if (!beamSensor.get() && ampTime.get() >= 1.5) {
                ampMotor.set(0);
                ampTime.stop();
            }
        }
        }

        //#AMPSCORE
        //This method will score in the amp manually
        public void ampScore() {
            if (IO.dController.getBButton()) {
                rightIntakeMotor.set(0.4);
                ampMotor.set(0.3);
            } else {
                rightIntakeMotor.set(0);
                ampMotor.set(0);
            }
        }


        //#MOVEMANIPULATOR
        //This method will move the manipulator forward
        public void moveManipulator(boolean isNegative) {
        if (!isNegative) {   
            if (IO.dController.getRightTriggerAxis() > 0.4) {
                rightBaseMotor.set(0.3);
            } else {
                rightBaseMotor.set(0);
            }
        } else {
            if (IO.dController.getLeftTriggerAxis() > 0.4) {
                rightBaseMotor.set(-0.3);
            } else {
                rightBaseMotor.set(0);
            }
        }
        }

        //#MOVEMANIPULATOR
        //This method will move the manipulator forward by a set time
        public void moveManipulator(double moveTime) {
            
            Timer moveTimer = new Timer();
            if (moveTimer.get() <= moveTime) {
                rightBaseMotor.set(0.3);
            } else {
                rightBaseMotor.set(0);
            }
        }



        //#CONTROLMANIPULATOR
        //This method will add keybinds for all the control methods in the manipulator class
        public void controlManipulator() {
            if (IO.dController.getAButton()) manualControl();
        if (usingDigitalSensors) {
            if (IO.dController.getLeftTriggerAxis() > 0.4) intake(4);
            if (IO.dController.getRightBumper()) moveManipulator(false);
            if (IO.dController.getYButton()) ampPosition(5);
            if (IO.dController.getXButton()) shootPosition(4);
            if (IO.dController.getBButton()) ampScore(4);
            if (IO.dController.getRightTriggerAxis() > 0.4) shootNote(3);
        } else {
            if (IO.dController.getRightBumper()) moveManipulator(false);
            if (IO.dController.getLeftBumper()) moveManipulator(true);
            if (IO.dController.getRightTriggerAxis() > 0.4) shootNote();
            if (IO.dController.getLeftTriggerAxis() > 0.4) intake();
            if (IO.dController.getBButton()) ampScore();
        }
        }



        //#AUTOMANIPULATOR
        //This method will do all of the actions for our manipulator during auto
        public void autoManipulator(boolean doesIntake, boolean doesAim, boolean doesShoot, boolean doesAmp) {
            if (doesIntake) {
                moveManipulator(1.5);
                intake(4);
            }
            if (doesAim) ampPosition(5);
            if (doesShoot) shootNote(3);
            if (doesAmp) ampScore(4);
        }
}