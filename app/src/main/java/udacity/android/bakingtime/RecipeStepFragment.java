package udacity.android.bakingtime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.Step;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepActivity}
 * on handsets.
 */
public class RecipeStepFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String STEP_KEY = "step-id";
    private static final String PLAY_POSITION_KEY = "play-position";

    private Step step;
    private SimpleExoPlayer player = null;
    private Uri videoUri = null;
    private long playPosition = C.TIME_UNSET;

    @BindView(R.id.video_player) SimpleExoPlayerView videoPlayer;
    @BindView(R.id.video_thumbnail) ImageView videoThumbnail;
    @BindView(R.id.step_description) TextView detail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepFragment() {
    }

    private Uri createVideoUri(String thumbnailUrl, String videoUrl) {
        boolean thumbnailUrlContainVideo = !thumbnailUrl.isEmpty() &&
                Objects.equals(thumbnailUrl.substring(thumbnailUrl.length() - 4), ".mp4");

        String notEmptyUrl = videoUrl.isEmpty() ? (thumbnailUrlContainVideo ? thumbnailUrl : "") :
                videoUrl;

        if (notEmptyUrl.isEmpty()) {
            videoPlayer.setVisibility(View.GONE);

            return null;
        }

        return Uri.parse(notEmptyUrl);
    }

    private void createVideoPlayer(Uri videoUri)
    {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        Context context = getContext();

        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        videoPlayer.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "Exotic Video Player"),
                bandwidthMeter
        );

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(
                videoUri,
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        );

        if (playPosition != C.TIME_UNSET) {
            player.seekTo(playPosition);
        }

        player.setPlayWhenReady(true);
        player.prepare(videoSource);
    }

    private void releaseVideoPlayer() {
        if (player != null) {
            playPosition = player.getContentPosition();

            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(STEP_KEY)) {
            step = arguments.getParcelable(STEP_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(PLAY_POSITION_KEY, playPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            playPosition = savedInstanceState.getLong(PLAY_POSITION_KEY, C.TIME_UNSET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (step != null) {
            videoUri = createVideoUri(step.thumbnailUrl, step.videoUrl);

            if (!step.thumbnailUrl.isEmpty()) {
                Picasso.with(getContext())
                        .load(step.thumbnailUrl)
                        .into(videoThumbnail);
            } else {
                videoThumbnail.setVisibility(View.GONE);
            }

            detail.setText(step.description);
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releaseVideoPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (videoUri != null) {
            createVideoPlayer(videoUri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        playPosition = player.getCurrentPosition();

        if (Util.SDK_INT <= 23) {
            releaseVideoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT > 23) {
            releaseVideoPlayer();
        }
    }
}
