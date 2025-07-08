package com.example.vibeifyfer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoodSongs {
    public static Map<String, List<String>> moodToTracks = new HashMap<>();

    static {

        moodToTracks.put("angry", Arrays.asList(
                "3OHfY25tqY28d16oZczHc8", // Kill Bill
                "6FB3v4YcR57y4tXFcdxI1E", // IKYWT
                "6KfoDhO4XUWSbnyKjNp9c4", // Maniac
                "2gyxAWHebV7xPYVxqoi86f", // Get Him Back
                "5lAnYvAIkSDNXqfo7DyFUm" // Aint Shit

        ));

        moodToTracks.put("sad", Arrays.asList(
                "1MWbRPUJxQ02ouC1TA6rFf", // Super Far
                "51ChrwmUPDJvedPQnIU8Ls", // Dive
                "7gqdZpe7MlTLA59viClLoY", // Backburner
                "7ArrTJ1LwcGAlEO65LUQ7i", // I dont wanna love you anymre
                "2Ch7LmS7r2Gy2kc64wv3Bz", // die for you
                "2QfznFotJNZmnIEYFdzE5T", //  heartbreak anniv
                "2aJnyNu4PQxQ2lyj5boiMG" // i hate you

        ));

        moodToTracks.put("neutral", Arrays.asList(
                "37IZHbmUmuhZITM4SW6PtV", // My Bubble Gum
                "6DCZcSspjsKoFjzjrWoCdn", // Gods Plan
                "3QS9ZCtoSCJhmaJ7QNXSAS", // Act II date @8
                "4AO1XhrgJczQ9bNVxdfKQe", // What To Do
                "47yD0e9MCRIB8dgUXPfyW3", // pop ur shit
                "3ruoIF2UnoXdzK8mR61ebq", // Rich N Shit
                "40iJIUlhi6renaREYGeIDS" // Fair Trade

        ));

        moodToTracks.put("happy", Arrays.asList(
                "5GQPBzoRBDxcAjwSSluQXD", // Pretty Little Baby
                "4VUwkH455At9kENOfzTqmF", // Beautiful
                "0wCnQp1p0J8KlMV7e1A7fC", // Fresh Eyes
                "6DzTaNNwdUXiJI1l0u6wNe", // Best Ive Ever Had
                "5EGPf0nqO7vEIwsOX6Er98", // Obsessed
                "67G6iaOw8DZqp1z8STR89R", // Whole Lotta Money
                "3JsydWaf2Ev4ehaLUjj3SY", // Confident
                "2GCKWEsbb0Xo1oodTOKVi1" // Heartbreaker

        ));

        moodToTracks.put("super happy", Arrays.asList(
                "39LLxExYz6ewLAcYrzQQyP", // Levitating
                "08HGey7e94Ez3ZurG1IXTP", // next door
                "1IIKrJVP1C9N7iPtG6eOsK", // Go Crazy
                "7ciLq0Cip0yxiz6KANrOUq", // Toothbrush
                "5aIVCx5tnk0ntmdiinnYvw" // Water

        ));


    }
}
