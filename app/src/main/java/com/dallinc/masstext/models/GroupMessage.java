package com.dallinc.masstext.models;

import com.orm.SugarRecord;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by dallin on 2/7/15.
 */
public class GroupMessage extends SugarRecord<GroupMessage> {
    public String sentAt;
    public String messageBody;
    public ArrayList<String> variables;
    public String variable_string;

    public GroupMessage() {

    }

    public GroupMessage(String body, ArrayList<String> vars) {
        DateTime dateTime = DateTime.now();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d, yyyy - h:mm a");
        sentAt = dateTime.toString(fmt);
        messageBody = body;
        variables = vars;
    }

    public void buildArrayListFromString() {
        variables = new ArrayList<String>();
        for(String variable: variable_string.split(", ")) {
            variables.add(variable);
        }
    }

    public void saveVariablesAsJson() {
        if(variables.size() < 1) {
            variable_string = "";
            return;
        }
        variable_string = variables.get(0);
        for(String variable: variables.subList(1, variables.size())) {
            variable_string += ", " + variable;
        }
    }

    @Override
    public void save() {
        saveVariablesAsJson();
        super.save();
    }
}
