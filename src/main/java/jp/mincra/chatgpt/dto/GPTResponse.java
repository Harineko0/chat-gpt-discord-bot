package jp.mincra.chatgpt.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "choices",
        "created",
        "id",
        "model",
        "object",
        "usage"
})
@Generated("jsonschema2pojo")
public class GPTResponse {

    @JsonProperty("choices")
    private List<Choice> choices;
    @JsonProperty("created")
    private Integer created;
    @JsonProperty("id")
    private String id;
    @JsonProperty("model")
    private String model;
    @JsonProperty("object")
    private String object;
    @JsonProperty("usage")
    private Usage usage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("choices")
    public List<Choice> getChoices() {
        return choices;
    }

    @JsonProperty("choices")
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    @JsonProperty("created")
    public Integer getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(Integer created) {
        this.created = created;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("object")
    public String getObject() {
        return object;
    }

    @JsonProperty("object")
    public void setObject(String object) {
        this.object = object;
    }

    @JsonProperty("usage")
    public Usage getUsage() {
        return usage;
    }

    @JsonProperty("usage")
    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "{\"GPTResponse\":{"
                + "\"choices\":" + choices
                + ", \"created\":\"" + created + "\""
                + ", \"id\":\"" + id + "\""
                + ", \"model\":\"" + model + "\""
                + ", \"object\":\"" + object + "\""
                + ", \"usage\":" + usage
                + "}}";
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.created == null)? 0 :this.created.hashCode()));
        result = ((result* 31)+((this.usage == null)? 0 :this.usage.hashCode()));
        result = ((result* 31)+((this.model == null)? 0 :this.model.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.choices == null)? 0 :this.choices.hashCode()));
        result = ((result* 31)+((this.object == null)? 0 :this.object.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GPTResponse) == false) {
            return false;
        }
        GPTResponse rhs = ((GPTResponse) other);
        return ((((((((this.created == rhs.created)||((this.created!= null)&&this.created.equals(rhs.created)))&&((this.usage == rhs.usage)||((this.usage!= null)&&this.usage.equals(rhs.usage))))&&((this.model == rhs.model)||((this.model!= null)&&this.model.equals(rhs.model))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.choices == rhs.choices)||((this.choices!= null)&&this.choices.equals(rhs.choices))))&&((this.object == rhs.object)||((this.object!= null)&&this.object.equals(rhs.object))));
    }

}