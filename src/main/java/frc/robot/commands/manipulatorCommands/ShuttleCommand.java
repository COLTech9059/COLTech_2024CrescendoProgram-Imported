package frc.robot.commands.manipulatorCommands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Manipulator;

public class ShuttleCommand extends Command{

    private Manipulator sentManip;
    private double startTime;
    private double endTime;
    private boolean finished = false;
    
    public ShuttleCommand(Manipulator sentManip, double startTime, double endTime)
    {
        this.sentManip = sentManip;
        this.startTime = startTime;
        this.endTime = endTime;

        addRequirements(sentManip);
    }

    private Timer shuttleTime = new Timer();
    @Override
    public void initialize()
    {
        shuttleTime.reset();
        shuttleTime.start();
        new ShootNote(sentManip, Constants.supplyPosition, Constants.supplySpeed, startTime, endTime);
        finished = false;
    }

    @Override
    public void execute()
    {
        if (shuttleTime.get() > endTime)
        {
            finished = true;
        }
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public void end(boolean interrupted)
    {
        sentManip.intakePosition(5, true);
    }
}
