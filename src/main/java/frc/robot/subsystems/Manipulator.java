package frc.robot.subsystems;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.IO;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;

public class Manipulator extends SubsystemBase
{
    
    //Create the LED class object
    // private LED led = new LED();

    //Create the gyro object
    // Pigeon2 gyro = new Pigeon2(Constants.gyroID);

    //Create the motor controller objects
    CANSparkMax leftBaseMotor = new CANSparkMax(Constants.leftBaseID, MotorType.kBrushless);
    CANSparkMax rightBaseMotor = new CANSparkMax(Constants.rightBaseID, MotorType.kBrushless);
    CANSparkMax ampMotor = new CANSparkMax(Constants.ampID, MotorType.kBrushless);
    CANSparkMax intakeMotor = new CANSparkMax(Constants.intakeID, MotorType.kBrushless);
    CANSparkMax followerAmpMotor = new CANSparkMax(Constants.ampFollowID, MotorType.kBrushless);

    //Create the encoder objects
    RelativeEncoder leftBaseEncoder = leftBaseMotor.getEncoder();
    RelativeEncoder rightBaseEncoder = rightBaseMotor.getEncoder();
    RelativeEncoder ampEncoder = ampMotor.getEncoder();
    RelativeEncoder followerAmpEncoder = followerAmpMotor.getEncoder();
    RelativeEncoder rightIntakeEncoder = intakeMotor.getEncoder();

    //Create the digital input objects
    static DigitalInput intakeSensor = new DigitalInput(Constants.intakeSensorID);

    //Reading true = Sensor not triggered, reading false = Sensor is triggered
    public DigitalInput frontSensor = new DigitalInput(Constants.frontSensorID); 
    public DigitalInput backSensor = new DigitalInput(Constants.backSensorID);

    public Manipulator()
    {
        //Set the followerAmpMotor as a follower
        followerAmpMotor.follow(ampMotor);

        //Set the leftBaseMotor as a follower
        leftBaseMotor.follow(rightBaseMotor);

        //Set the encoders to 0, effectively resetting them
        resetEncoders();

        rightBaseMotor.setOpenLoopRampRate(0.275);
    }

    //Encoder Methods

    //#RESETENCODERS
    //This method resets the encoder values to 0
    public void resetEncoders()
    {
        rightBaseEncoder.setPosition(0);
        leftBaseEncoder.setPosition(0);
        ampEncoder.setPosition(0);
        rightIntakeEncoder.setPosition(0);
        followerAmpEncoder.setPosition(0);
    }

    private double armAvg = 0;
    //#GETARMAVERAGE
    //Returns the average between the two arm motors, hopefully giving a more accurate value.
    public double GetArmAverage()
    {
        armAvg = (rightBaseEncoder.getPosition() + leftBaseEncoder.getPosition()) / 2;
        return (rightBaseEncoder.getPosition() + leftBaseEncoder.getPosition()) / 2;
    }

    //#SETARMENCODERS
    //Sets both arm encoders to the same value
    public void setArmEncoders(double value)
    {
        rightBaseEncoder.setPosition(value);
        leftBaseEncoder.setPosition(value);
    }

    //Position Methods

    //Variables associated:
    private static Timer posTimer = new Timer();

    //#INTAKEPOSITION
    //This method will run the manipulator base motors until the magnetic sensor is triggered at the amp spitting position
    /*
     * @Param timeout               The time limit for the method
     * @Param isMoving              Whether or not the method does anything
     * @Return didIntakePosition    Returns true if successfully positioned, and false if not
     */
    public boolean intakePosition(double timeout, boolean isMoving) 
    {
        if (isMoving) {posTimer.start(); canHold = false;}

        if (posTimer.get() > 0)
        {
            if (posTimer.get() <= timeout) 
            {
                //Check to see if the front sensor changed
                if (!frontSensor.get()) 
                {
                    rightBaseMotor.set(0);
                    resetEncoders();
                    // led.setBoard("green");
                    posTimer.stop();
                    posTimer.reset();

                    return true;
                }
                //Continue moving the arm until the above statement is satisfied.
                // if (GetArmAverage() <= 25 && frontSensor.get()) {
                //     rightBaseMotor.set(0.5);
                // }
                // else if (GetArmAverage() > 25 && frontSensor.get()) {
                // rightBaseMotor.set(0.8);
                // }
                rightBaseMotor.set(0.80);
            } 
            else
            {
                rightBaseMotor.set(0);

                posTimer.stop();
                posTimer.reset();
                // led.setBoard("red");
            }
        }
        return false;
    }


    //Variables associated:
    private static Timer shootPosTime = new Timer();

    //#SHOOTPOSITION
    //This method will bring the manipulator to a position for it to shoot from
    /*
     * @Param timeout            The time limit for the method
     * @Param isActive           Whether or not the method does anything
     * @Return didShootPosition  Returns true if successfully positioned, returns false if not
     */
    public boolean variablePosition(double timeout, boolean isActive, double targetPos) 
    {
        if (isActive) {shootPosTime.start();}
        
        if (shootPosTime.get() > 0)
        {
            if (shootPosTime.get() <= timeout) 
            {
                if (GetArmAverage() < targetPos - 1) {rightBaseMotor.set(0.35);} 
                else if (GetArmAverage() > targetPos + 1) {rightBaseMotor.set(-0.35);} 
                else
                {
                    rightBaseMotor.set(0);
                    //Set canHold to true so arm is held in place.
                    canHold = true;
                    //Reset timers.
                    shootPosTime.stop();
                    shootPosTime.reset();
                    rumbleController(IO.oController, RumbleType.kBothRumble, 0.5, 1);
                    return true;
                    // led.setBoard("green");
                }
            } 
            else 
            {
                rightBaseMotor.set(0);

                shootPosTime.stop();
                shootPosTime.reset();
                // led.setBoard("red");
            }
        }
        return false;
    }

    public void rumbleController(XboxController control, RumbleType type, double strength, double duration)
    {
        control.setRumble(type, strength);
        new WaitCommand(duration);
        control.setRumble(type, 0);
    }


    private double returnAngle = 0;
    private double calculateShootAngle(double armAxleToTargetWallDist) 
    {
        // Constants.armBodyLength * Math.cos(returnAngle)

        // Logic has not been added yet
        return returnAngle;
    }

    private boolean finished = false;
    private double desiredAngle = Constants.shootAngle;
    public boolean shootAngle() 
    {
        finished = false;
        if (!angleCalc.getBoolean(false)) desiredAngle = Constants.shootAngle;
        if (angleCalc.getBoolean(false)) desiredAngle = calculateShootAngle(0);

        if (GetArmAverage() < desiredAngle - 2) rightBaseMotor.set(0.25); 
        else if (GetArmAverage() > desiredAngle + 2) rightBaseMotor.set(-0.25); 
        else
        {
            rightBaseMotor.set(0);
            //Set canHold to true so arm is held in place.
            canHold = true;
            finished = true;
            // led.setBoard("green");
        }
        return finished;
    }

    //Variables associated:
    private static Timer ampPosTime = new Timer();

    //#AMPPOSITION
    //This method will bring the manipulator to a position for it to score in the amp
    /*
     * @param timeout           The time limit for the method
     * @param isActive          Whether or not the method does anything
     * @return didAmpPosition   Returns true if positioned successfully, returns false if not
     */
    public boolean ampPosition(double timeout, boolean isActive) 
    {
        if (isActive) {ampPosTime.start(); canHold = false;}
        
        if (ampPosTime.get() > 0) 
        {
            if (ampPosTime.get() <= timeout) 
            {
                if (backSensor.get()) 
                {
                    rightBaseMotor.set(0);
                    setArmEncoders(-95 + 20);

                    ampPosTime.stop();
                    ampPosTime.reset();

                    return true;
                    // led.setBoard("green");
                }            
                rightBaseMotor.set(-0.75);
            } 
            else if (ampPosTime.get() > timeout)
            {
                rightBaseMotor.set(0);

                ampPosTime.stop();
                ampPosTime.reset();
                // led.setBoard("red");
            }
        }
        return false;
    }

    //#MOVEARM
    // @Param armPower      The percent rotation speed of the motor
    public void moveArm(double ArmPower)
    {
        //Reset canHold when the trigger(s) are pressed.
        if (Math.abs(ArmPower) > 0) 
        {
            canHold = false;
            //Set arm power.
            rightBaseMotor.set(-ArmPower);
            if (!frontSensor.get() && -ArmPower > 0) rightBaseMotor.set(0);
            else if (backSensor.get() && -ArmPower < 0) rightBaseMotor.set(0);
        }   
        else if (Math.abs(ArmPower) == 0 && !canHold) rightBaseMotor.set(0);
    }

    public void testArm(double ArmPower)
    {
        //Reset canHold when the trigger(s) are pressed.
        if (Math.abs(ArmPower) > 0) 
        {
            canHold = false;
            //Set arm power.
            ArmPower = -ArmPower;
            //As arm gets closer to sensors (Encoder position), slow it down
            double encoderDistFromBottom = GetArmAverage();
            if(encoderDistFromBottom < 15 && ArmPower < 0)
            {
                double percentOfPower = (encoderDistFromBottom/15);
                if(percentOfPower < .3) percentOfPower  = .3;
                ArmPower *= percentOfPower;
            }

            if (!frontSensor.get() && ArmPower > 0) ArmPower = 0;
            else if (backSensor.get() && ArmPower < 0) ArmPower = 0;
        }   
        else if (Math.abs(ArmPower) == 0 && !canHold) ArmPower = 0;
        rightBaseMotor.set(ArmPower);
    }

    //Variables associated:
    private Timer scoreTime = new Timer();
    private boolean didShoot = false;

    //
    /*
    *@Param isActive      Whether or not the method does anything
    */
    private boolean shootOverride = false;
    public boolean shootNote(boolean isActive, double shootPower)
    {
        if (isActive)
        {
            scoreTime.start();
            ampMotor.set(shootPower);
        }
        if (scoreTime.get() < 0.5 && scoreTime.get() > 0) {intakeMotor.set(0); shootOverride = false; didShoot = false;} 
        if (scoreTime.get() >= 0.5 && scoreTime.get() < 0.75) {shootOverride = true; didShoot = false; intakeMotor.set(-0.4);}
        if (scoreTime.get() > 1.5)
        {
            shootOverride = false;

            intakeMotor.set(0);
            ampMotor.set(0);

            scoreTime.stop();
            scoreTime.reset();

            didShoot = true;
        }
        return didShoot;
    }

    private boolean returnBool = false;
    private boolean didIPos = false;
    private boolean didSPos = false;
    private boolean didSupply = false;
    private boolean didIPos2 = false;
    private Timer supplyTime = new Timer();
    public boolean supplyShot(boolean enabled, double timeout) {
        if (enabled) {supplyTime.start(); canHold = false;}
        if (supplyTime.get() > 0)
        {
            if (supplyTime.get() <= timeout)
            {
                if (intakePosition(5, true) && !didIPos)
                {
                    didIPos = true;
                    if (didIPos)
                    {
                        if (variablePosition(5, true, Constants.supplyPosition) && !didSPos)
                        {
                            didSPos = true;
                            if (didSPos)
                            {
                                if (shootNote(true, Constants.supplySpeed) && !didSupply)
                                {
                                    didSupply = true;
                                    if (didSupply)
                                    {
                                        if (intakePosition(5, true) && !didIPos2)
                                        {
                                            didIPos2 = true;
                                            if (didIPos2)
                                            {
                                                returnBool = true;
                                                supplyTime.stop();
                                                supplyTime.reset();
                                                didIPos = false;
                                                didSPos = false;
                                                didSupply = false;
                                                didIPos2 = false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {supplyTime.stop(); supplyTime.reset();}
        }
        return returnBool;
    }
    //#REVUPFLYWHEEL
    //An alternative method that only ramps up the flywheel, nothing else.
    //Used mostly for autonomous.
    public void revUpFlywheel(boolean enable, double speed){
        if (enable) ampMotor.set(speed);
        else ampMotor.set(0);
    }

    //Variables associated
    private Timer aScoreTime = new Timer();

    //#AMPSCORE
    /*
        * @Param isActive      Whether or not the method does anything
        */
    public void ampScore(boolean isActive)
    {
        if (isActive)
        {
            aScoreTime.start();
            ampMotor.set(0.3);
        }
        if (aScoreTime.get() < 0.3 && aScoreTime.get() > 0) shootOverride = false;
        if (aScoreTime.get() >= 0.3 && aScoreTime.get() < 0.75) {shootOverride = true; intakeMotor.set(-0.4);}
        if (aScoreTime.get() > 0.75)
        {
            shootOverride = false;

            intakeMotor.set(0);
            ampMotor.set(0);

            aScoreTime.stop();
            aScoreTime.reset();
        }
    }

    public void eStop(boolean isActive) 
    {
        if (isActive)
        {
            intakeMotor.set(0);
            ampMotor.set(0);
            rightBaseMotor.set(0);
        }
    }

    private Timer drivePosTime = new Timer();
    private boolean didDrivePosition = false;

    //#DRIVEPOSITION
    public boolean drivePosition(double timeout, boolean isActive)
    {
            if (isActive) drivePosTime.start();
    
        if (drivePosTime.get() <= timeout && drivePosTime.get() > 0) 
        {
            if (rightBaseEncoder.getPosition() < Constants.drivePosition - 1) 
            {
                rightBaseMotor.set(0.4);
            } 
            else if (rightBaseEncoder.getPosition() > Constants.drivePosition + 0.5)
            {
                rightBaseMotor.set(-0.4);
            } 
            else if (rightBaseEncoder.getPosition() >= Constants.drivePosition - 1 && rightBaseEncoder.getPosition() <= Constants.drivePosition + 0.5)
            {
                rightBaseMotor.set(0);
                didDrivePosition = true;

                drivePosTime.stop();
                drivePosTime.reset();
                // led.setBoard("green");
            }
        } 
        else 
        {
            rightBaseMotor.set(0);
            didDrivePosition = false;

            drivePosTime.stop();
            drivePosTime.reset();
            // led.setBoard("red");
        }
    return didDrivePosition;
    }

    //#RUNINTAKE
    /*
        * @Param isReverse     Whether or not the intake motors are reversed
        * @Param isActive      Whether or not the method does anything
        */
    public void runIntake(boolean isReverse, boolean isActive, boolean inUse)
    {
        if (inUse) {
            if (!isReverse && isActive) intakeMotor.set(-.4);
            else if (isReverse && !isActive) intakeMotor.set(.2);
            else if (shootOverride) intakeMotor.set(-0.4);
            else if (!shootOverride) intakeMotor.set(0);
            else intakeMotor.set(0);
        }
    }

    //#PECKINTAKE
    /*
    * @Param isActive       Whether or not the method activates
    */
    public boolean peckIntake(boolean isActive)
    {
        if (isActive)
        {
            if (intakePosition(5, true))
            {
                rightBaseEncoder.setPosition(0);
                if (intake(5, true))
                {

                }
            }
        }   
        return false;
    }

    //Variables associated:
    private static Timer intakeTime = new Timer();

    //#INTAKE
    //This method will intake a note
    /*
        * @Param timeout       The time limit of the method
        * @Param doesIntake    Whether or not the method does anything
        */
    public boolean intake(double timeout, boolean doesIntake) 
    {
        if (doesIntake) {intakeTime.start();}

        if (intakeTime.get() > 0)
        {
            if (intakeTime.get() <= timeout) 
            {
                if (!intakeSensor.get()) {intakeMotor.set(-0.4);} 
                else
                {
                    intakeMotor.set(0);
                    
                    intakeTime.stop();
                    intakeTime.reset();

                    return true;
                }
            } 
            else 
            {
                intakeMotor.set(0);

                intakeTime.stop();
                intakeTime.reset();
            }
        }
        return false;
    }

    //Periodic Functions

    //Variables associated
    private boolean canHold = false;
    private boolean frontStatus = false;
    private boolean opticalStatus = false;

    //#HOLDMANIPULATOR
    //If the boolean "canHold" is set, runs this segment to keep the arm stable.
    public void holdManipulator(boolean Override){
    if (Override || canHold) {rightBaseMotor.set(-0.03); /*setArmEncoders(Constants.shootPosition);*/}
    }

    private BooleanSupplier opticalSupplier = () -> opticalStatus;
    private BooleanSupplier frontSupplier = () -> frontStatus;
    private BooleanSupplier backSupplier = () -> backSensor.get();
    private BooleanSupplier cHSupplier = () -> canHold;
    private DoubleSupplier avgSupplier = () -> GetArmAverage();

    private ShuffleboardTab manipulatorTab = Shuffleboard.getTab("Manipulator");

    private SuppliedValueWidget<Boolean> optical =
            manipulatorTab.addBoolean("Optical Sensor", opticalSupplier)
            .withSize(1, 1)
            .withPosition(0, 0);

    private SuppliedValueWidget<Boolean> front =
            manipulatorTab.addBoolean("Front Sensor", frontSupplier)
            .withSize(1, 1)
            .withPosition(1, 0);

    private SuppliedValueWidget<Boolean> back =
            manipulatorTab.addBoolean("Back Sensor", backSupplier)
            .withSize(1, 1)
            .withPosition(2, 0);

    private SuppliedValueWidget<Boolean> cHold =
            manipulatorTab.addBoolean("Can Hold", cHSupplier)
            .withSize(1, 1)
            .withPosition(0, 1);

    private SuppliedValueWidget<Double> manipArmAvg =
            manipulatorTab.addDouble("Manipulator Arm Encoder Average", avgSupplier)
            .withSize(1, 1)
            .withPosition(1, 1);
            
    private ComplexWidget intakeCam =
            manipulatorTab.add(CameraServer.startAutomaticCapture())
            .withSize(3, 3)
            .withPosition(6, 0);

    // private ComplexWidget pigeon =
    //         manipulatorTab.add(gyro)
    //         .withSize(1, 1)
    //         .withPosition(4, 0);

    GenericEntry angleCalc =
          Shuffleboard.getTab("Manipulator")
          .add("Calculate Shoot Angle?", false)
          .withPosition(0, 3)
          .withSize(2, 1)
          .withWidget("Toggle Button")
          .getEntry();

    // GenericEntry resetGyro =
    //     Shuffleboard.getTab("Manipulator")
    //     .add("Reset Encoders", false)
    //     .withPosition(2, 3)
    //     .withSize(1, 1)
    //     .withWidget(BuiltInWidgets.kBooleanBox)
    //     .getEntry();

    // SimpleWidget accelerometer =
    //     Shuffleboard.getTab("Manipulator")
    //     .add("Accelerometer", gyro.getAccelerationX())
    //     .withSize(1, 1)
    //     .withPosition(4, 3);
    
    //#MANIPULATORDASHBOARD
    //This method updates the dashboard with all the data from the manipulator class
    public void manipulatorDashboard() 
    {
        if (intakeSensor.get()) opticalStatus = false;
        else opticalStatus = true;

        if (frontSensor.get()) frontStatus = false;
        else frontStatus = true;

        // opticalSupplier = () -> opticalStatus;
        // frontSupplier = () -> frontStatus;
        backSupplier = () -> backSensor.get();
        // cHSupplier = () -> canHold;

        //Push the digital sensor data to the shuffleboard
        // SmartDashboard.putBoolean("Beam Sensor", intakeSensor.get());
        // SmartDashboard.putBoolean("Front Sensor", frontSensor.get());
        // SmartDashboard.putBoolean("Back Sensor", backSensor.get());
        // SmartDashboard.putBoolean("Is Holding", canHold);

        // //Push the encoder values to shuffleboard
        // SmartDashboard.putNumber("Manipulator Arm Encoder Average", GetArmAverage());
    }

    public StatusSignal armAngle;
    //#PERIODIC
    //Runs functions within this file periodically (about every ~20ms).
    @Override
    public void periodic()
    {
        holdManipulator(false);
        manipulatorDashboard();

        // armAngle = gyro.getYaw();

        if (!frontSensor.get()) resetEncoders();
        if (backSensor.get()) setArmEncoders(-95 + 20);
    }
}
