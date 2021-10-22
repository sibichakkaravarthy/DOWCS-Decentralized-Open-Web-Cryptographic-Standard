package me.twodee.dowcspws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultObject {
    public boolean isSuccessful;
    public Object obj;
    public String error;
}
