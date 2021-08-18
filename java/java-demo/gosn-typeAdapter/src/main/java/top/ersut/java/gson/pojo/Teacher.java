package top.ersut.java.gson.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.bind.DateTypeAdapter;
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
public class Teacher {

    private String name;

    private LocalDateTime updateAt;

    @JsonAdapter(DateTypeAdapter.class)
    private Date createAt;

}
