package top.ersut.java.gson.pojo;

import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ersut.java.gson.TimestampTypeAdapter;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private String name;

    @JsonAdapter(TimestampTypeAdapter.LocalDateTimeTypeAdapter.class)
    private LocalDateTime updateAt;

    @JsonAdapter(TimestampTypeAdapter.DateTypeAdapter.class)
    private Date createAt;

}
