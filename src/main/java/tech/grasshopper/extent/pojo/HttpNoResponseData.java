package tech.grasshopper.extent.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpNoResponseData extends HttpResponseData implements HttpNoData {

}
