package contactpicker;

/**
 * Created by dallin on 2/7/15.
 */
public class ContactGroup {
    private String groupName;
    private String groupId;
    boolean selected = false;

    public ContactGroup() { }

    public ContactGroup(String id, String name) {
        this.groupId = id;
        this.groupName = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupName(String name) {
        this.groupName = name;
    }

    public void setGroupId(String id) {
        this.groupId = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
