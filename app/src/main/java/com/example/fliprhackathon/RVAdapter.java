package com.example.fliprhackathon;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
//    JSONArray jsonArray;
    String adapterType;
    JSONObject jsonObject;
    private static final String TAG = "RVAdapter";

    public RVAdapter(Context context, JSONObject jsonObject, String adapterType) {
        this.context = context;
        this.jsonObject = jsonObject;
        this.adapterType = adapterType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (adapterType) {
            case "upcoming_matches":
                View view = inflater.inflate(R.layout.upcoming_matches_card, parent, false);
                return new UpcomingMatchesHolder(view);

            case "team_1":
                View view1 = inflater.inflate(R.layout.players_name_card, parent, false);
                return new Team1Holder(view1);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (adapterType) {
            case "upcoming_matches":
                UpcomingMatchesHolder upcomingMatchesHolder = (UpcomingMatchesHolder) holder;
                try {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray teamName = info.getJSONArray("teams");
                    upcomingMatchesHolder.Team1.setText(teamName.getString(0));
                    upcomingMatchesHolder.Team2.setText(teamName.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "team_1":
                Team1Holder team1Holder = (Team1Holder) holder;
                try {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray teamName = info.getJSONArray("teams");
                    team1Holder.name.setText(teamName.getString(0));
                    team1Holder.points.setText(teamName.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "team_2":
                Team2Holder team2Holder = (Team2Holder) holder;
                try {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray teamName = info.getJSONArray("teams");
                    team2Holder.name.setText(teamName.getString(0));
                    team2Holder.points.setText(teamName.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "your_11":
                Your11Holder your11Holder = (Your11Holder) holder;
                try {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray teamName = info.getJSONArray("teams");
                    your11Holder.name.setText(teamName.getString(0));
                    your11Holder.points.setText(teamName.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
//        return jsonObject.length();
        return 1;
    }

    public class UpcomingMatchesHolder extends RecyclerView.ViewHolder {
        TextView Team1, Team2;
        Button btn;

        public UpcomingMatchesHolder(@NonNull View itemView) {
            super(itemView);

            Team1 = itemView.findViewById(R.id.team1name);
            Team2 = itemView.findViewById(R.id.team2name);
            btn =itemView.findViewById(R.id.create_team_btn);

            btn.setOnClickListener(v -> {
//                Log.d(TAG, "UpcomingMatchesHolder: hello this is " + getAdapterPosition() + itemView.getContext());
                Intent intent = new Intent(context, BuildTeamActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("data", jsonObject.toString());
                context.startActivity(intent);
            });
        }
    }

    public class Team1Holder extends RecyclerView.ViewHolder {
        TextView name, points;

        public Team1Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.playerName);
            points = itemView.findViewById(R.id.playerPoints);

            itemView.setOnClickListener(v -> {
                name.setVisibility(View.GONE);
                points.setVisibility(View.GONE);
            });
        }
    }

    public class Team2Holder extends RecyclerView.ViewHolder {
        TextView name, points;

        public Team2Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.playerName);
            points = itemView.findViewById(R.id.playerPoints);

            itemView.setOnClickListener(v -> {
                name.setVisibility(View.GONE);
                points.setVisibility(View.GONE);
            });
        }
    }

    public class Your11Holder extends RecyclerView.ViewHolder {
        TextView name, points;

        public Your11Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.playerName);
            points = itemView.findViewById(R.id.playerPoints);

            itemView.setOnClickListener(v -> {
                name.setVisibility(View.GONE);
                points.setVisibility(View.GONE);
            });
        }
    }
}
