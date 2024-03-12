package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.Manipulator;

public class SpeakerScore extends Command{
    private final Manipulator m_Manipulator;
    private final Timer moveTime = new Timer();
    private final double startTime;
    private final double endTime;
    private final boolean enableArm;
    private final boolean armDirection;
    private boolean Completed = false;
    public SpeakerScore(Manipulator manip, boolean enableArm, boolean armDirection, double start, double end){
        m_Manipulator = manip;
        startTime = start;
        endTime = end;
        this.armDirection = armDirection;
        this.enableArm = enableArm;

        addRequirements(m_Manipulator);
    }
    @Override
    public void initialize(){
        double directionMultiplier = -1;
        if (armDirection) directionMultiplier = 1;
        if (enableArm) m_Manipulator.moveArm(-.3 * directionMultiplier);
        m_Manipulator.shootNote(true);
        moveTime.start();
    }
    @Override
    public void execute(){
        if (moveTime.get() >= startTime)
        {
            m_Manipulator.runIntake(false, true);
        }
        if (moveTime.get() >= endTime)
        {
            m_Manipulator.runIntake(false, false);
            m_Manipulator.shootNote(false);
            Completed = true;
        }
    }
    @Override
    public boolean isFinished(){
        return Completed;
    }
    @Override
    public void end(boolean interrupted){
        Completed = false;
        m_Manipulator.shootNote(false);
        m_Manipulator.runIntake(false, false);
        m_Manipulator.moveArm(0.0);

        moveTime.stop();
        moveTime.reset();
    }
}
