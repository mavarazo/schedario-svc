package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.Tag;
import ch.mav.schedario.repository.TagRepository;
import ch.mav.schedario.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }
}
