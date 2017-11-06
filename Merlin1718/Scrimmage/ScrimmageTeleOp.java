package org.firstinspires.ftc.teamcode.team.Merlin1718.Scrimmage;

import android.hardware.TriggerEventListener;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.sql.Driver;

@TeleOp(name = "Scrimmage", group = "Scrimmage")
//@Disabled //Uncomment this if it is not wanted on the phone
public class ScrimmageTeleOp extends ScrimmageMeathods {
    private ScrimmageHardware robot = new ScrimmageHardware();//The hardware map needs to be the hardware map of the robot we are using

    public void init(){//This only runs once
        super.init();//Initializing everything needed
    }
    @Override
    public void init_loop(){//Tis runs many time during the init phase
    }
    @Override
    public void start(){}//This runs when the start button is pressed
    @Override
    public void loop(){//This runs while opmode is active
        drive(joyValues());
        glyph();
    }
    @Override
    public void stop(){}


}