/* 
 * Copyright (C) 2017 Navdeep Singh Sidhu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package in.co.s13.marking.assistant.meta;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import javafx.scene.paint.Color;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class FeedBackEntry implements Serializable {

    public static enum EntryType {
        FEEDBACK, SECTION_START, SECTION_END
    };
    private double maximumMarks, minimumMarks, obtainedMarks;
    private String feedBack;
    private boolean duplicateAllowed;
    private EntryType type;
    private int indent;
    private int id;

    public FeedBackEntry() {
    }

    public FeedBackEntry(int id, double maximumMarks, double minimumMarks, double obtainedMarks, String feedBack, boolean duplicateAllowed, EntryType type, int indent) {
        this.maximumMarks = maximumMarks;
        this.minimumMarks = minimumMarks;
        this.obtainedMarks = obtainedMarks;
        this.feedBack = feedBack;
        this.duplicateAllowed = duplicateAllowed;
        this.type = type;
        this.indent = indent;
        this.id = id;
    }

    public FeedBackEntry(int id, FeedBackEntry fb) {
        this.maximumMarks = fb.getMaximumMarks();
        this.minimumMarks = fb.getMinimumMarks();
        this.obtainedMarks = fb.getObtainedMarks();
        this.feedBack = fb.getFeedBack();
        this.duplicateAllowed = fb.isDuplicateAllowed();
        this.type = fb.getType();
        this.indent = fb.getIndent();
        this.id = id;
    }

    public double getMaximumMarks() {
        return maximumMarks;
    }

    public void setMaximumMarks(double maximumMarks) {
        this.maximumMarks = maximumMarks;
    }

    public double getMinimumMarks() {
        return minimumMarks;
    }

    public void setMinimumMarks(double minimumMarks) {
        this.minimumMarks = minimumMarks;
    }

    public double getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(double obtainedMarks) {
        if (obtainedMarks < minimumMarks) {
            this.obtainedMarks = minimumMarks;

        } else if (obtainedMarks > maximumMarks) {
            this.obtainedMarks = maximumMarks;
        } else {
            this.obtainedMarks = obtainedMarks;
        }
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public boolean isDuplicateAllowed() {
        return duplicateAllowed;
    }

    public void setDuplicateAllowed(boolean duplicateAllowed) {
        this.duplicateAllowed = duplicateAllowed;
    }

    public EntryType getType() {
        return type;
    }

    public void setType(EntryType type) {
        this.type = type;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Color getColor() {
        double minm=this.minimumMarks;
        if (this.minimumMarks == -9999) {
            minm = 0;
        }
        double ratio = (getObtainedMarks()-minm) / (this.maximumMarks - minm);
//        System.out.println("Max: "+maximumMarks+", Min: "+minimumMarks+" Smin:"
//                +minm+" ratio: "+ratio);
        if (ratio > .8) {
            return Color.DARKGREEN;
        } else if (ratio <= .8 && ratio > .6) {
            return Color.GREEN;
        } else if (ratio <= .6 && ratio > .4) {
            return Color.YELLOWGREEN;
        } else if (ratio <= .4 && ratio > .2) {
            return Color.ORANGE;
        } else {
            return Color.RED;

        }

    }

    public double getDiff() {
        return getMaximumMarks() - getObtainedMarks();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeedBackEntry other = (FeedBackEntry) obj;
        if (this.maximumMarks != other.maximumMarks) {
            return false;
        }
        if (this.minimumMarks != other.minimumMarks) {
            return false;
        }
        if (this.obtainedMarks != other.obtainedMarks) {
            return false;
        }
        if (this.duplicateAllowed != other.duplicateAllowed) {
            return false;
        }
        if (!Objects.equals(this.feedBack, other.feedBack)) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("     ");
        }
        if (this.type == EntryType.FEEDBACK) {
            sb.append("     ");
        }
        StringBuilder formattedFeedback = new StringBuilder();
        if (feedBack.contains("\n")) {
            String feedBackLines[] = feedBack.split("\n");
            for (int i = 0; i < feedBackLines.length; i++) {
                String feedBackLine = feedBackLines[i];
                if (i == 0) {
                    formattedFeedback.append(feedBackLine).append("\n");
                } else {
                    formattedFeedback.append(sb.toString()).append(feedBackLine).append("\n");
                }
            }
        } else {
            formattedFeedback.append(feedBack);

        }
        if (!(this.type == EntryType.FEEDBACK && maximumMarks == 0 && minimumMarks == 0)) {
            sb.append("[" + "").append(obtainedMarks).append("/").append(maximumMarks).append("]" + " ");
        }
        sb.append(formattedFeedback.toString());
        return sb.toString();
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("id", id);
        obj.put("minm", minimumMarks);
        obj.put("maxm", maximumMarks);
        obj.put("obtm", obtainedMarks);
        obj.put("type", type);
        obj.put("clonable", duplicateAllowed);
        obj.put("feedBack", feedBack);
        return obj;
    }

    public FeedBackEntry(JSONObject obj) {
        id = obj.getInt("id");
        minimumMarks = obj.getDouble("minm");
        maximumMarks = obj.getDouble("maxm");
        obtainedMarks = obj.getDouble("obtm");
        type = EntryType.valueOf(obj.getString("type"));
        duplicateAllowed = obj.getBoolean("clonable");
        feedBack = obj.getString("feedBack");
    }

    enum FeedBackEntryComparator implements Comparator<FeedBackEntry> {

        FEEDBACK_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return o1.getFeedBack().compareTo(o2.getFeedBack());
            }
        },
        ID_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }
        },
        INDENT_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return Integer.valueOf(o1.getIndent()).compareTo(o2.getIndent());
            }
        },
        MAX_MARKS_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return Double.valueOf(o1.getMaximumMarks()).compareTo(o2.getMaximumMarks());
            }
        },
        MIN_MARKS_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return Double.valueOf(o1.getMinimumMarks()).compareTo(o2.getMinimumMarks());
            }
        },
        OBT_MARKS_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return Double.valueOf(o1.getObtainedMarks()).compareTo(o2.getObtainedMarks());
            }
        },
        TYPE_SORT {
            public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                return (o1.getType()).compareTo(o2.getType());
            }
        };

        public static Comparator<FeedBackEntry> decending(final Comparator<FeedBackEntry> other) {
            return new Comparator<FeedBackEntry>() {
                public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                    return -1 * other.compare(o1, o2);
                }
            };
        }

        public static Comparator<FeedBackEntry> getComparator(final FeedBackEntryComparator... multipleOptions) {
            return new Comparator<FeedBackEntry>() {
                public int compare(FeedBackEntry o1, FeedBackEntry o2) {
                    for (FeedBackEntryComparator option : multipleOptions) {
                        int result = option.compare(o1, o2);
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                }
            };
        }
    }

}
