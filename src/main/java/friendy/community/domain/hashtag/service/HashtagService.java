package friendy.community.domain.hashtag.service;

import friendy.community.domain.hashtag.model.Hashtag;
import friendy.community.domain.hashtag.repository.HashtagRepository;
import friendy.community.domain.hashtag.repository.PostHashtagRepository;
import friendy.community.domain.post.model.Post;
import friendy.community.domain.post.model.PostHashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public void saveHashtags(Post post, List<String> hashtagNames) {
        List<Hashtag> existTags = hashtagRepository.findAllByNameIn(hashtagNames);
        List<String> existNames = getExistHashtagNames(existTags);

        List<Hashtag> newHashtags = getOnlyNewHashags(existNames, hashtagNames);
        List<Hashtag> savedNewHashtags = hashtagRepository.saveAll(newHashtags);

        existTags.addAll(savedNewHashtags);
        savePostHashtags(post, existTags);
    }

    public void updateHashtags(Post post, List<String> hashtags) {
        deleteHashtags(post.getId());
        saveHashtags(post, hashtags);
    }

    public void deleteHashtags(Long postId) {
        postHashtagRepository.deleteAllByPostId(postId);
    }

    private List<String> getExistHashtagNames(List<Hashtag> existTags) {
        return existTags.stream()
                .map(Hashtag::getName)
                .toList();
    }

    private List<Hashtag> getOnlyNewHashags(List<String> existNames, List<String> hashtagNames) {
        return hashtagNames.stream()
                .distinct()
                .filter(name -> !existNames.contains(name))
                .map(Hashtag::new)
                .toList();
    }

    private void savePostHashtags(Post post, List<Hashtag> hashtags) {
        List<PostHashtag> postHashtags = hashtags.stream()
                .map(hashtag -> new PostHashtag(post, hashtag))
                .toList();
        postHashtagRepository.saveAll(postHashtags);
    }

}
