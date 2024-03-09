package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

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
    // RelativeEncoder leftBaseEncoder = leftBaseMotor.getEncoder();
    RelativeEncoder rightBaseEncoder = rightBaseMotor.getEncoder();
    RelativeEncoder ampEncoder = ampMotor.getEncoder();
    RelativeEncoder followerAmpEncoder = followerAmpMotor.getEncoder();
    RelativeEncoder rightIntakeEncoder = intakeMotor.getEncoder();

    //Create the digital input objects
    static DigitalInput intakeSensor = new DigitalInput(Constants.intakeSensorID);

    //Reading true = Sensor not triggered, reading false = Sensor is triggered
    DigitalInput frontSensor = new DigitalInput(Constants.frontSensorID); 
    DigitalInput backSensor = new DigitalInput(Constants.backSensorID);

    private boolean usingDigitalSensors = true;

    public Manipulator()
    {
        //Reset the digital sensors boolean
        usingDigitalSensors = true;

        didIntakePosition = false;
        didAmpPosition = false;
        didShootPosition = false;

        //Reset intake boolean
        if (!intakeSensor.get()) {didIntake = false;} else {didIntake = true;}

        //Set the followerAmpMotor as a follower
        followerAmpMotor.follow(ampMotor);

        //Set the leftBaseMotor as a follower
        leftBaseMotor.follow(rightBaseMotor);

        //Set the encoders to 0, effectively resetting them
        // leftBaseEncoder.setPosition(0);
        rightBaseEncoder.setPosition(0);
        ampEncoder.setPosition(0);
        rightIntakeEncoder.setPosition(0);
        followerAmpEncoder.setPosition(0);

        rightBaseMotor.setOpenLoopRampRate(0.25);
    }

    //#MANUALCONTROL
    //This method toggles the use of digital sensors duting teleop
    public void manualControl() 
    {
        if (usingDigitalSensors) {usingDigitalSensors = false;} else {usingDigitalSensors = true;}
    }


    //#RESETENCODERS
    //This method resets the encoder values to 0
    public void resetEncoders()
    {
        rightBaseEncoder.setPosition(0);
    }
    
    private static boolean didIntakePosition = false;
    private static Timer posTimer = new Timer();

    //#INTAKEPOSITION
    //This method will run the manipulator base motors until the magnetic sensor is triggered at the amp spitting position
    /*
     * @Param timeout               The time limit for the method
     * @Param isMoving              Whether or not the method does anything
     * @Return didIntakePosition    Returns true if successfully positioned, and false if not
     */
    
     private boolean intakePosEnabled = false;

    public boolean intakePosition(double timeout, boolean isMoving) 
    {
        

        if (isMoving) {posTimer.start(); intakePosEnabled = true;}

        if (intakePosEnabled)
        {
            if (posTimer.get() <= timeout && posTimer.get() > 0) 
            {
                if (intakeSensor.get()) 
                {
                rightBaseMotor.set(0.3);
                } 
                else if (!intakeSensor.get()) 
                {
                    intakePosEnabled = false;

                rightBaseMotor.set(0);
                rightBaseEncoder.setPosition(0);

                didIntakePosition = true;
                // led.setBoard("green");
                posTimer.stop();
                posTimer.reset();
                }
            } 
            else 
            {
                intakePosEnabled = false;

            rightBaseMotor.set(0);
            didIntakePosition = false;
            // led.setBoard("red");
            }
        }
        return didIntakePosition;
    }


    
    private static Timer shootPosTime = new Timer();
    private boolean didShootPosition = false;

    //#SHOOTPOSITION
    //This method will bring the manipulator to a position for it to shoot from
    /*
     * @Param timeout            The time limit for the method
     * @Param isActive           Whether or not the method does anything
     * @Return didShootPosition  Returns true if successfully positioned, returns false if not
     */

     private boolean shootPosEnabled;

    public boolean shootPosition(double timeout, boolean isActive) 
    {
        if (isActive) {shootPosTime.start(); shootPosEnabled = true;}
        
        if (shootPosEnabled)
        {
            if (shootPosTime.get() <= timeout && shootPosTime.get() > 0) 
            {
                if (rightBaseEncoder.getPosition() < Constants.shootPosition - 2) 
                {
                    rightBaseMotor.set(0.15);
                } 
                else if (rightBaseEncoder.getPosition() > Constants.shootPosition + 1)
                {
                    rightBaseMotor.set(-0.2);
                } 
                else if (rightBaseEncoder.getPosition() >= Constants.shootPosition - 2 && rightBaseEncoder.getPosition() <= Constants.shootPosition + 1)
                {
                    shootPosEnabled = false;
                    rightBaseMotor.set(0);
                    didShootPosition = true;
                    // led.setBoard("green");
                }
            } 
            else 
            {
                shootPosEnabled = false;
                rightBaseMotor.set(0);
                didShootPosition = false;
                // led.setBoard("red");
            }
        }
        return didShootPosition;
    }


    private static Timer ampPosTime = new Timer();
    private boolean didAmpPosition = false;

    //#AMPPOSITION
    //This method will bring the manipulator to a position for it to score in the amp
    /*
     * @Param timeout           The time limit for the method
     * @Param isActive          Whether or not the method does anything
     * @Return didAmpPosition   Returns true if positioned successfully, returns false if not
     */

     private boolean ampPosEnabled = false;

    public boolean ampPosition(double timeout, boolean isActive) 
    {
        if (isActive) {ampPosTime.start(); ampPosEnabled = true;}
        
        if (ampPosEnabled) 
        {
            if (ampPosTime.get() <= timeout && ampPosTime.get() > 0) 
            {
                if (!backSensor.get()) 
                {
                    rightBaseMotor.set(-0.3);
                } 
                else 
                {
                    ampPosEnabled = false;
                    rightBaseMotor.set(0);
                    didAmpPosition = true;
                    // led.setBoard("green");
                }
            } 
            else 
            {
                ampPosEnabled = false;
                rightBaseMotor.set(0);
                didAmpPosition = false;
                // led.setBoard("red");
            }
        }
        return didAmpPosition;
    }


        //#MANIPULATORDASHBOARD
        //This method updates the dashboard with all the data from the manipulator class
        public void manipulatorDashboard() 
        {
            //Tell if it is in manual or not
            SmartDashboard.putBoolean("Is Manual?", !usingDigitalSensors);

            //Push the digital sensor data to the shuffleboard
            SmartDashboard.putBoolean("Beam Sensor", intakeSensor.get());
            SmartDashboard.putBoolean("Front Sensor", frontSensor.get());
            SmartDashboard.putBoolean("Back Sensor", backSensor.get());

            //Push the encoder values to shuffleboard
            SmartDashboard.putNumber("Manipulator Base Encoder", rightBaseEncoder.getPosition());
        }

        //#ARMPOWER
        // @Param armPower      The percent rotation speed of the motor
        public void moveArm(double ArmPower)
        {
            if (!frontSensor.get() && -ArmPower < 0) rightBaseMotor.set(0);
            if (backSensor.get() && -ArmPower > 0) rightBaseMotor.set(0);
            rightBaseMotor.set(-ArmPower);
        }



        private Timer scoreTime = new Timer();

        //#SHOOTNOTE
        /*
        *@Param isActive      Whether or not the method does anything
        */
        public void shootNote(boolean isActive)
        {
            if (isActive)
            {
                scoreTime.start();
                ampMotor.set(0.5);
            } 
                if (scoreTime.get() < 0.3 && scoreTime.get() > 0) intakeMotor.set(0);
                if (scoreTime.get() >= 0.3 && scoreTime.get() < 0.75) intakeMotor.set(-0.4);
                if (scoreTime.get() > 0.75)
                {
                    intakeMotor.set(0);
                    ampMotor.set(0);

                    scoreTime.stop();
                    scoreTime.reset();
                }
        }


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


        public static boolean didIntake = false;
        private static Timer intakeTime = new Timer();

        //#INTAKE
        //This method will intake a note
        /*
         * @Param timeout       The time limit of the method
         * @Param doesIntake    Whether or not the method does anything
         */
        private boolean enabled;
        public void intake(double timeout, boolean doesIntake) 
        {
            if (doesIntake) {intakeTime.start(); enabled = true;}

            if (enabled)
            {
                if (intakeTime.get() <= timeout && intakeTime.get() > 0) 
                {
                    if (!intakeSensor.get()) 
                    {
                        intakeMotor.set(-0.4);
                    } 
                    else
                    {
                        intakeMotor.set(0);
                        didIntake = true;
                        enabled = false;

                        intakeTime.stop();
                        intakeTime.reset();
                    }
                } 
                else 
                {
                    intakeMotor.set(0);
                    didIntake = false;
                    enabled = false;

                    intakeTime.stop();
                    intakeTime.reset();
                }
            }
           
        }



        


        //#AUTOSHOOT
        //@Param shootTime      The duration for which the motors will run
        private Timer shootTimer = new Timer();
        public void autoShoot(double shootTime)
        {
            shootTimer.start();

            ampMotor.set(0.6);
            if (shootTimer.get() < shootTime + 1.5 && shootTimer.get() > 1.5) 
            {
                intakeMotor.set(0.3);
            }
            else if (shootTimer.get() > shootTime + 1.5)
            {
                intakeMotor.set(0);
                ampMotor.set(0);
                shootTimer.stop();
                shootTimer.reset();
            }
        }



        //#MOVEMANIPULATOR
        //This method will move the manipulator forward by a set time
        /*
         * @Param moveTime              The time for which the manipulator will move
         * @Param isNegative            Determines whether the robot will move in a positive or negative direction
         * @Return didMoveManipulator   Returns true when finished moving
         */
        private boolean didMoveManipulator = false;
        public boolean moveManipulator(double moveTime, boolean isNegative) 
        {
            Timer moveTimer = new Timer();
            moveTimer.reset();
            moveTimer.start();

            if (!isNegative) 
            {
                if (moveTimer.get() <= moveTime) 
                {
                rightBaseMotor.set(-0.2);
                } 
                else 
                {
                rightBaseMotor.set(0);
                didMoveManipulator = true;
                }
            } 
            else   
            {
                Timer moveTimer2 = new Timer();
                moveTimer2.reset();
                moveTimer2.start();

                if (moveTimer2.get() <= moveTime) 
                {
                rightBaseMotor.set(0.2);
                } else 
                {
                rightBaseMotor.set(0);
                didMoveManipulator = true;
                }
            }
            return didMoveManipulator;
        }


        
        boolean setPos = false;
        double holdPos = 0;

    //#HOLDMANIPULATOR
    /*
     *@Param isHolding      Whether or not the method does anything
     */
    public void holdManipulator(boolean isHolding) 
    {
        if (isHolding) rightBaseMotor.set(0.05);
    }


        //#AUTOMANIPULATOR
        //This method will do all of the actions for our manipulator during auto
        /*
         * @Param doesIntake        If true, then the manipulator will go to the intake position and intake a note
         * @Param doesAim           If true, then the manipulator will go to the shooting position
         * @Param doesShoot         If true, then the manipulator will shoot a note
         * @Param doesAmpAim        If true, then the manipulator will go to the amp position
         * @Param doesAmp           If true, then the manipulator will score in the amp
         */
        public void autoManipulator(boolean doesIntake, boolean doesAim, boolean doesShoot, boolean doesAmpAim, boolean doesAmp) 
        {
            if (doesIntake) 
            {
                if (intakePosition(5, true)) intake(3, true);
            }
            if (doesAim) shootPosition(5, true);
            if (doesShoot) shootNote(true);
            if (doesAmpAim) ampPosition(5, true);
            if (doesAmp) ampScore(true);
        }
}
