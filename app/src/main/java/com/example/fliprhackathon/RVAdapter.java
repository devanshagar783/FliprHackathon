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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.UpcomingMatchesHolder> {

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
    public RVAdapter.UpcomingMatchesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (adapterType) {
            case "upcoming_matches":
                View view = inflater.inflate(R.layout.upcoming_matches_card, parent, false);
                return new UpcomingMatchesHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.UpcomingMatchesHolder holder, int position) {
        switch (adapterType) {
            case "upcoming_matches":
                try {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray teamName = info.getJSONArray("teams");
                    holder.Team1.setText(teamName.getString(0));
                    holder.Team2.setText(teamName.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                Toast.makeText(itemView.getContext(), "This is working", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "UpcomingMatchesHolder: hello this is " + getAdapterPosition() + itemView.getContext());
                Intent intent = new Intent(context, BuildTeamActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("data", (Parcelable) jsonObject);
                context.startActivity(intent);
            });
        }
    }
}
