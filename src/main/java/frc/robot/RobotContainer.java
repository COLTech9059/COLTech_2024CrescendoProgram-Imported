package frc.robot;

import frc.robot.subsystems.*;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
// import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.*;
import frc.robot.commands.driveCommands.DriveCommand;
import frc.robot.commands.manipulatorCommands.ArmCommand;
import frc.robot.commands.manipulatorCommands.ShootNote;
import frc.robot.commands.manipulatorCommands.ShuttleCommand;

public class RobotContainer {
    //Subsystem declarations.
    public final DriveTrain m_DriveTrain = new DriveTrain();
    public final LimeLight m_LimeLight = new LimeLight();
    public final Manipulator m_Manipulator = new Manipulator();
    //Setup Controller.
    // private final XboxController xbContMovement = new XboxController(0);
    // private final XboxController xbContArm = new XboxController(1);
    //Autonomous Command.
    // private final Command m_autonomousCommand = new Autonomous(m_DriveTrain, m_LimeLight, m_Manipulator, Robot.autoID);

    public RobotContainer()
    {

        // SmartDashboard.putData("Autonomous Command", new Autonomous(m_DriveTrain, m_LimeLight, m_Manipulator, aChooser.getInteger(4)));
        // SmartDashboard.putData("Drive Forward Command", new MoveForwardInches(m_DriveTrain, m_Manipulator, moveSpeed.getDouble(0.5), moveDist.getDouble(36), false));
        // SmartDashboard.putData("Reset Position Command", new ResetPosition(m_Manipulator));
        // SmartDashboard.putData("Speaker Score Command", new SpeakerScore(m_Manipulator, eArm, aDirection, start.getDouble(2.2), end.getDouble(3)));
        // SmartDashboard.putData("Timed Turn Command", new TurnWithTimer(m_DriveTrain, tPower.getDouble(0), tTime.getDouble(0)));

        JoystickButton rBumperO = new JoystickButton(IO.oController, XboxController.Button.kB.value);
        rBumperO.onTrue(new ShuttleCommand(m_Manipulator, 0.4, 1.25));

        m_DriveTrain.setDefaultCommand
        (
            new DriveCommand
            (
                m_DriveTrain, 
                () -> IO.dController.getLeftY(), 
                () -> IO.dController.getRightX()
            )
        );

        m_Manipulator.setDefaultCommand
        (
            new ArmCommand
            (
                m_Manipulator, 
                () -> (IO.dController.getRightTriggerAxis() - IO.dController.getLeftTriggerAxis()), 
                () -> IO.oController.getRightBumper(), 
                () -> (IO.dController.getLeftBumper()),
                () -> IO.dController.getAButton(),
                () -> IO.oController.getLeftBumper(),
                () -> IO.oController.getAButton(),
                () -> IO.oController.getYButton(),
                () -> IO.oController.getXButton()
             )
        );
        //Future possible commands here.
    }

    ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");

        GenericEntry aChooser =
          Shuffleboard.getTab("Manipulator")
          .add("Autonomous Chooser", 4)
          .withPosition(0, 2)
          .getEntry();

    //For getting the autonomous command.
    public Command getAutoCommand()
    {
        Autonomous newAuto = new Autonomous(m_DriveTrain, m_LimeLight, m_Manipulator, aChooser.getInteger(4));
        return newAuto;
    }

    private Sendable sendDT = m_DriveTrain;
    private Sendable sendManip = m_Manipulator;

    private GenericEntry moveSpeed =
            Shuffleboard.getTab("SmartDashboard")
            .add("Move Speed", 0.5)
            .getEntry();
            
    private GenericEntry moveDist =
            Shuffleboard.getTab("SmartDashboard")
            .add("Move Distance", 36)
            .getEntry();

    private boolean eArm = false;
    private GenericEntry enableArm =
            Shuffleboard.getTab("SmartDashboard")
            .add("Move Arm", 0)
            .getEntry();

    private boolean aDirection = false;
    private GenericEntry armDirection =
            Shuffleboard.getTab("SmartDashboard")
            .add("Arm Direction", 0)
            .getEntry();

    private GenericEntry start =
            Shuffleboard.getTab("SmartDashboard")
            .add("Start Time", 2.2)
            .getEntry();

    private GenericEntry end =
            Shuffleboard.getTab("SmartDashboard")
            .add("End Time", 3)
            .getEntry();

    private GenericEntry tPower =
            Shuffleboard.getTab("SmartDashboard")
            .add("Turn Power", 0)
            .getEntry();

    private GenericEntry tTime =
            Shuffleboard.getTab("SmartDashboard")
            .add("Turn Time", 0)
            .getEntry();

    public void subsystemDashboard()
    {
        SmartDashboard.putData("DriveTrain", sendDT);
        SmartDashboard.putData("Manipulator", sendManip);

        if (enableArm.getDouble(0) == 0) eArm = false;
        else eArm = true;

        if (armDirection.getDouble(0) == 0) aDirection = false;
        else aDirection = true;
    }
}
