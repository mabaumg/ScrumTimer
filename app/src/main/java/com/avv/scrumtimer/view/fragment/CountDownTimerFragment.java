package com.avv.scrumtimer.view.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.avv.scrumtimer.Participant;
import com.avv.scrumtimer.R;
import com.avv.scrumtimer.view.ControlsView;
import com.avv.scrumtimer.view.FontManager;
import com.avv.scrumtimer.view.MemoryCache;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CountDownTimerFragment extends Fragment {

    @BindView(R.id.play)
    Button play;

    @BindView(R.id.stop)
    Button stop;

    @BindView(R.id.next)
    Button next;

    @BindView(R.id.countdowntimer)
    ControlsView countDownTimer;

    @BindView(R.id.participantShift)
    TextView participantShift;

    private List<Participant> participants;
    private int currentShift;

    protected PowerManager.WakeLock mWakeLock;

    public CountDownTimerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CountDownTimerFragment newInstance() {
        CountDownTimerFragment fragment = new CountDownTimerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.countdown_fragment, container, false);
        ButterKnife.bind(this, view);

        final PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "countdown");
        mWakeLock.acquire();

        Typeface iconFont = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(view.findViewById(R.id.icons_container), iconFont);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    isPlaying = false;
                    play.setText(getResources().getString(R.string.fa_play));
                    countDownTimer.pause();
                } else {
                    isPlaying = true;
                    play.setText(getResources().getString(R.string.fa_pause));
                    countDownTimer.play();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = false;
                play.setText(getResources().getString(R.string.fa_play));
                countDownTimer.stop();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentName = "";
                if(currentShift<participants.size()) {
                    currentName = participants.get(currentShift).getName();
                }
                currentShift++;
                if(currentShift<participants.size()){
                    participantShift.setText(participants.get(currentShift).getName());
                    long totalTime = countDownTimer.stop();
                    MemoryCache.setResult(currentName, totalTime);
                }
                play.setText(getResources().getString(R.string.fa_play));
            }
        });

        participants = MemoryCache.getParticipants(getActivity());
        currentShift = 0;
        if(currentShift < participants.size()) {
            participantShift.setText(participants.get(currentShift).getName());
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mWakeLock.release();
        super.onDestroy();
    }
}
