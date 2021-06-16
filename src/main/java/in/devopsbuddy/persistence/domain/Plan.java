package in.devopsbuddy.persistence.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import in.devopsbuddy.enums.PlanEnum;

@Entity
public class Plan implements Serializable {

    @Id
    private int id;

    private String name;

    public Plan() {
    }

    public Plan(PlanEnum planEnum) {
        this.id = planEnum.getId();
        this.name = planEnum.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Plan other = (Plan) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
