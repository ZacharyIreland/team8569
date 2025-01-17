/*
 * This squares us against the wall, it could be used but deemed unnecessary
 * due to the gyro on the NavX sensor
 */

package org.firstinspires.ftc.teamcode.team.Other;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.team.Merlin1617.Merlin1.Merlin1Hardware;

import com.qualcomm.robotcore.util.Range;


@TeleOp(name = "wall square", group = "Merlin1")//This is the name of the code and what will be seen on the robot phone
@Disabled //Uncomment this if it is not wanted on the phone
public class WallSquareFunction extends LinearOpMode {


    /* Declare OpMode members. */
    Merlin1Hardware robot           = new Merlin1Hardware();//Setting up the Hardware map

    @Override
    public void runOpMode() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //It is ready to run
        telemetry.update();//Updates and displays data on the screen.
        waitForStart();//whit for the start button to be pressed


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {


            telemetry.update();


            double LeftRange;
            double RightRange;
            double RangeDifference;
            double RangeDifferenceScaled;
            double Motor1WallSquarePower;
            double Motor2WallSquarePower;
            double Motor3WallSquarePower;
            double Motor4WallSquarePower;
            


            /* Wall Squaring Steps
            1. get the difference between the values the sensors are giving you
                int RangeDifference0
            2. Scale the values down (Value x 0.1 or 0.2)
                RangeDifference0 x 0.2 = RangeDifferenceScaled
                0.2 plus or minus difference = Right Motor Power
                    0.2 +(-) RangeDifferenceScaled = RMotorPower
                0.2 plus or minus difference = Left Motor Power
                    0.2 +(-) RangeDifferenceScaled = LMotorPower
            Extra Lines Possibly needed
                LeftMotorValue = Range.clip(Motorvalue, -1, 1)
                RightMotorValue = Range.clip(MotorValue, -1. 1)
            Range Sensors are on the side of the robot with motor 2 on right and motor 3 on left
            */



            RightRange = robot.rangeSensor.getDistance(DistanceUnit.CM);//left range sensor
            LeftRange = robot.rangeSensor2.getDistance(DistanceUnit.CM);//right range sensor
            RangeDifference = RightRange - LeftRange;


            if(RangeDifference == 0) {

                robot.Motor1.setPower(0);
                robot.Motor2.setPower(0);
                robot.Motor3.setPower(0);
                robot.Motor4.setPower(0);
                telemetry.addData("Robot is", "Square");
                telemetry.update();

            }
            else {

                RangeDifferenceScaled = RangeDifference * .05;

                RangeDifferenceScaled = Range.clip(RangeDifferenceScaled, 1, -1);

                Motor1WallSquarePower = RangeDifferenceScaled;
                Motor2WallSquarePower = RangeDifferenceScaled;
                Motor3WallSquarePower = RangeDifferenceScaled;
                Motor4WallSquarePower = RangeDifferenceScaled;

                robot.Motor1.setPower(-Motor1WallSquarePower);
                robot.Motor2.setPower(-Motor2WallSquarePower);
                robot.Motor3.setPower(Motor3WallSquarePower);
                robot.Motor4.setPower(Motor4WallSquarePower);
                telemetry.addData("Robot is", "Working");
                telemetry.update();
            }
            telemetry.addData("Right range", RightRange);
            telemetry.addData("left range", LeftRange);






            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
            robot.waitForTick(40);
        }
    }

}
