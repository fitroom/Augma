package world.augma.ui.circle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.User;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;
import world.augma.work.Utils;
import world.augma.work.visual.OnSwipeTouchListener;

public class UICirclePage extends AppCompatActivity {

    //userID, username, circleID, circleNAME
    private String circleID;
    private String circleName;
    private String circleDescription;
    private TextView circlePageCircleName;
    private TextView swipeRightText;
    private TextView circleDescriptionField;
    private LottieAnimationView background;
    private LottieAnimationView swipeView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_circle_page);
        ServiceUIMain serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
        user =  serviceUIMain.fetchUser();

        circleID = getIntent().getExtras().getString("circleID");
        circleName = getIntent().getExtras().getString("name");
        circleDescription = getIntent().getExtras().getString("desc");




        background = findViewById(R.id.circlePageBackground);
        swipeRightText = findViewById(R.id.swipeRightText);
        circlePageCircleName = (TextView) findViewById(R.id.circlePageCircleName);
        circleDescriptionField = (TextView) findViewById(R.id.circleDescription);

        circleDescriptionField.setText(circleDescription);
        circlePageCircleName.setText(circleName);

        background.setAnimation(R.raw.gradient_animated_background);
        background.setRepeatCount(LottieDrawable.INFINITE);
        background.playAnimation();

        if(!user.checkCircleMembership(circleID)){
            swipeView = findViewById(R.id.circlePageSwipeView);
            swipeView.setAnimation(R.raw.swipe_left_to_right);
            swipeView.setRepeatCount(LottieDrawable.INFINITE);
            swipeView.setScale(0.7f);
            swipeView.playAnimation();

            swipeView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
                @Override
                public void onSwipeRight() {
                    super.onSwipeRight();

                    AWS aws = new AWS();
                    ServiceUIMain serviceUIMain1 = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();

                    try {

                        if(aws.execute(AWS.Service.JOIN_CIRCLE,user.getUserID(),user.getUsername(),circleID,circleName).get()){
                            Utils.sendSuccessNotification(UICirclePage.this, "YAAAY!");
                            serviceUIMain1.refreshUser();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else{
            swipeRightText.setText("You are already a member of this circle.");
        }


    }
}
