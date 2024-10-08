package frc.robot.subsystems;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;

public class Manipulator extends SubsystemBase
{
    
    //Create the LED class object
    // private LED led = new LED();

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
               rightBaseMotor.set(0.9);
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
    public boolean shootPosition(double timeout, boolean isActive) 
    {
        if (isActive) {shootPosTime.start();}
        
        if (shootPosTime.get() > 0)
        {
            if (shootPosTime.get() <= timeout) 
            {
                if (GetArmAverage() < Constants.shootPosition - 1) {rightBaseMotor.set(0.25);} 
                else if (GetArmAverage() > Constants.shootPosition + 1) {rightBaseMotor.set(-0.25);} 
                else
                {
                    rightBaseMotor.set(0);
                    //Set canHold to true so arm is held in place.
                    canHold = true;
                    //Reset timers.
                    shootPosTime.stop();
                    shootPosTime.reset();
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
                rightBaseMotor.set(-0.80);
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


    //Variables associated:
    private Timer scoreTime = new Timer();
    private boolean didShoot = false;

    //#SHOOTNOTE
    /*
    *@Param isActive      Whether or not the method does anything
    */
    public boolean shootNote(boolean isActive)
    {
        if (isActive)
        {
            scoreTime.start();
            ampMotor.set(0.65);
        }
        if (scoreTime.get() < 0.3 && scoreTime.get() > 0) {intakeMotor.set(0); didShoot = false;} 
        if (scoreTime.get() >= 0.3 && scoreTime.get() < 0.75) {intakeMotor.set(-0.4); didShoot = false;}
        if (scoreTime.get() > 0.75)
        {
            intakeMotor.set(0);
            ampMotor.set(0);

            scoreTime.stop();
            scoreTime.reset();

            didShoot = true;
        }
        return didShoot;
    }

    //#REVUPFLYWHEEL
    //An alternative method that only ramps up the flywheel, nothing else.
    //Used mostly for autonomous.
    public void revUpFlywheel(boolean enable){
        if (enable) ampMotor.set(.70);
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
        if (aScoreTime.get() < 0.3 && aScoreTime.get() > 0) intakeMotor.set(0);
        if (aScoreTime.get() >= 0.3 && aScoreTime.get() < 0.75) intakeMotor.set(-0.4);
        if (aScoreTime.get() > 0.75)
        {
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
    public void runIntake(boolean isReverse, boolean isActive)
    {
        if (!isReverse && isActive) intakeMotor.set(-.4);
        else if (isReverse && !isActive) intakeMotor.set(.2);
        else intakeMotor.set(0);
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
        if (Override || canHold) {rightBaseMotor.set(-0.03); setArmEncoders(Constants.shootPosition);}
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
            .withPosition(4, 0);
    
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

    //#PERIODIC
    //Runs functions within this file periodically (about every ~20ms).
    @Override
    public void periodic()
    {
        holdManipulator(false);
        manipulatorDashboard();

        if (!frontSensor.get()) resetEncoders();
        if (backSensor.get()) setArmEncoders(-95 + 20);
    }
}
