package com.dallinc.masstexter;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by dallin on 1/31/15.
 */
public class Template extends SugarRecord<Template> {
    String title;
    String body;
    ArrayList<String> variables;
    String variable_string;

    public Template() {
    }

    public Template(String t, String b, ArrayList<String> v) {
        this.title = t;
        this.body = b;
        this.variables = v;
    }

    public void buildArrayListFromString() {
        this.variables = new ArrayList<String>();
        for(String variable: this.variable_string.split(", ")) {
            this.variables.add(variable);
        }
    }

    public void saveVariablesAsJson() {
        if(this.variables.size() < 1) {
            this.variable_string = "";
            return;
        }
        this.variable_string = this.variables.get(0);
        for(String variable: variables.subList(1, variables.size())) {
            this.variable_string += ", " + variable;
        }
    }

    @Override
    public void save() {
        saveVariablesAsJson();
        super.save();
    }
}
