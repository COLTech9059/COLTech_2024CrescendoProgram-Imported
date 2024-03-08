// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.IO;
import frc.robot.Robot;


public class DriveTrain extends SubsystemBase 
{
  /** Creates a new ExampleSubsystem. */

    // private Robot robot = new Robot();

    //create motor controller objects
    private static CANSparkMax leftP = new CANSparkMax(2, MotorType.kBrushless);
    private static CANSparkMax rightP = new CANSparkMax(3, MotorType.kBrushless);
    private static CANSparkMax leftF = new CANSparkMax(4, MotorType.kBrushless);
    private static CANSparkMax rightF = new CANSparkMax(5, MotorType.kBrushless);
  
    //create encoder objects
    static RelativeEncoder leftEncoder = leftP.getEncoder();
    static RelativeEncoder rightEncoder = rightP.getEncoder();
  
    // Create the differential drive object
    public final DifferentialDrive HamsterDrive = new DifferentialDrive(leftP, rightP);

    double forwardPower;

  public DriveTrain() {}

  //#STOPDRIVE
  //Method to stop the drive train
  public void stopDrive() 
  {
    HamsterDrive.arcadeDrive(0, 0, false);
  }


  Timer turnTimer = new Timer();

  public void drive(double forwardPow, double turnPow) 
  {
    HamsterDrive.arcadeDrive(forwardPow *.9, turnPow *-.8, false);
  }

  //#AUTODRIVE
  //This method drives the auto for _ amount of time in a + or - direction
  /* @Param speed     The forward speed of the robot, can be negative
   * @Param distance  The distance that the robot will drive
   * @Param turn      The speed at which the robot will turn, must be used seperately from regular driving
   * @Param turnTime  The length of time for which the robot will turn
  */ 
  public void autoDrive(double speed, double distance, double turn, double turnTime) 
  {
    distance = Math.abs(distance);
    rightDistance = 0;
    turnTimer.reset();
    turnTimer.start();
    rightEncoder.setPosition(0);
    leftEncoder.setPosition(0);
    rightDistance = Math.abs(rightEncoder.getPosition());

    //Drive with positive distance
    if (distance > 0 && rightDistance < distance) 
    {
      HamsterDrive.arcadeDrive(speed, 0, false);
    } 
    else if (rightDistance >= distance) 
    {
      HamsterDrive.arcadeDrive(0, 0, false);
    }

    //Turn
    if (turnTimer.get() < turnTime && turn != 0) 
    {
      HamsterDrive.arcadeDrive(0, turn, false);
    } 
    else if (turnTimer.get() >= turnTime && turn != 0) 
    {
      HamsterDrive.arcadeDrive(0, 0, false);
    }
  }

  //#RESETDRIVE
  //This method resets the drive train elements
  public void resetDrive() 
  {

    // Reset the factory defaults for the motor controllers
    // leftP.restoreFactoryDefaults();
    // rightP.restoreFactoryDefaults();
    // leftF.restoreFactoryDefaults();
    // rightF.restoreFactoryDefaults(); 

    // Set up the motor controller followers
    leftF.follow(leftP);
    rightF.follow(rightP);

    
    // Invert the right side motor controller
    rightP.setInverted(true);

    //Disable the safety feature of the drivetrain, which can be very difficult to work around
    HamsterDrive.setSafetyEnabled(false);

    // Set deadband for the differential drive
    HamsterDrive.setDeadband(0.1);

    //Set the encoder positions to zero, effectively resetting them
    leftEncoder.setPosition(0);
    rightEncoder.setPosition(0);

    //Set the motors to accelerate and decelerate slower
    rightP.setOpenLoopRampRate(0.25);
    leftP.setOpenLoopRampRate(0.25);
  }



      static double rightWheelRotations = 0;
      static double leftWheelRotations = 0;

      static double rightDistance = 0;
      static double leftDistance = 0;

      //#ENCODERMATH
      //This fucntion handles all of the math and data necessary to use the encoders
      public static void encoderMath() 
      {
        //All the math to convert encoder rotations to horizontal distance in inches
        rightWheelRotations = rightEncoder.getPosition() / 8.45;
        leftWheelRotations = leftEncoder.getPosition() / 8.45;

        rightDistance = rightWheelRotations * 18;
        leftDistance = leftWheelRotations * 18;

        // Displays the Left and Right encoder rates on the dashboard with the specified names
        SmartDashboard.putNumber("Left Encoder Distance", leftDistance);
        SmartDashboard.putNumber("Right Encoder Distance", rightDistance);
      }



      //#DRIVE
      //This method determines what to do with the motors based on the controller input
      public void drive() 
      {
        // Get the value of the Y-Axis on the joystick
        double forward = IO.dController.getLeftY();

        // Adjust Speed/Power so that it will always be at a max of 80%
        double change = 0;

        if (forward < 0) change = 0.1;
        if (forward > 0) change = -0.1;

        forwardPower = forward + change;

        // Set turn to the value of the X-Axis on the joystick
        double turn = IO.dController.getRightX();

        // Reduce Turn Power
        double turnPower = turn *= 0.8;

        // Drive the Robot with <forwardPower> and <turnPower>
        if (IO.dController.getRightX() > 0.1 || IO.dController.getRightX() < -0.1 || IO.dController.getLeftY() > 0.1 || IO.dController.getLeftY() < -0.1) 
        {
        HamsterDrive.arcadeDrive(forwardPower, -turnPower);
        } 
        else if ( (IO.dController.getLeftY() > -0.1 && IO.dController.getLeftY() < 0.1) && (IO.dController.getRightX() < 0.1 && IO.dController.getRightX() > -0.1) ) 
        {
        stopDrive();
        }
      }
}
