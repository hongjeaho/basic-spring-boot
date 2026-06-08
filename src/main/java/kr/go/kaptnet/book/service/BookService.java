package kr.go.kaptnet.book.service;

import kr.go.kaptnet.book.dto.BookDto;
import kr.go.kaptnet.book.dto.BookSearchCondition;
import kr.go.kaptnet.book.dto.BookSearchRequest;
import kr.go.kaptnet.book.exception.BookNotFoundException;
import kr.go.kaptnet.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;

    public List<BookDto> getAllBooks() {
        return bookMapper.findAll();
    }

    public BookDto getBookById(Long id) {
        BookDto book = bookMapper.findById(id);
        if (book == null || book.getId() == null) {
            throw new BookNotFoundException(id);
        }
        return book;
    }

    public List<BookDto> searchBooks(BookSearchCondition condition) {
        return bookMapper.search(condition);
    }

    public List<BookDto> advancedSearch(BookSearchRequest request) {
        return bookMapper.advancedSearch(request);
    }
}
