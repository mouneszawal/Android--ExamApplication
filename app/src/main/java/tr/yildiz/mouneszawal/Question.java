package tr.yildiz.mouneszawal;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String quesTitle;
    private String[] options;
    private int rightAnsIndex;
    private String additions;

    public String getAdditions() {
        return additions;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    public Question(String quesTitle, String[] options, int rightAnsIndex, String additions) {
        this.quesTitle = quesTitle;
        this.options = options;
        this.rightAnsIndex = rightAnsIndex;
        this.additions = additions;
    }


    public String getQuesTitle() {
        return quesTitle;
    }

    public void setQuesTitle(String quesTitle) {
        this.quesTitle = quesTitle;
    }

    public int getRightAnsIndex() {
        return rightAnsIndex;
    }

    public void setRightAnsIndex(int rightAnsIndex) {
        this.rightAnsIndex = rightAnsIndex;
    }
    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

}
