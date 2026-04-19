package de.htw_berlin.beMindful;

public class ActivityEntry  {
        String title;
        boolean done;

    public ActivityEntry() {}

    public ActivityEntry(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }
    }


