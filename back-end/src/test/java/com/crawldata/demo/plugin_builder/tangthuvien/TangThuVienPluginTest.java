package com.crawldata.demo.plugin_builder.tangthuvien;

import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.tangthuvien.TangThuVienPlugin;
import com.crawldata.back_end.response.DataResponse;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TangThuVienPluginTest {
    @InjectMocks
    private TangThuVienPlugin tangThuVienPlugin;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGetAuthorIdFromValidUrl() {
        String url = "https://truyen.tangthuvien.vn/tac-gia?author=12345";
        String authorId = tangThuVienPlugin.getAuthorIdFromUrl(url);
        assertEquals("12345",authorId);
    }

    @Test
    public void testGetAuthorIdFromInvalidUrl() {
        String url = "this is an invalid url";
        String authorId = tangThuVienPlugin.getAuthorIdFromUrl(url);
        assertNull(authorId);
    }

    @Test
    public void testGetNovelIdFromUrl() {
        String url = "https://truyen.tangthuvien.vn/doc-truyen/dao-gia-muon-phi-thang-dao-gia-yeu-phi-thang";
        String novelId = tangThuVienPlugin.getNovelIdFromUrl(url);
        assertEquals("dao-gia-muon-phi-thang-dao-gia-yeu-phi-thang", novelId);
    }

    @Test
    public void testGetTotalChapterFromTextSuccess() {
        String text = "Danh sách chương (100 chương)";
        Integer totalChapters = tangThuVienPlugin.getTotalChapterFromText(text);
        assertEquals(100, totalChapters);
    }

    @Test
    public void testGetTotalChapterFromTextError() {
        String text = "Danh sách chương 100 chương";
        Integer totalChapters = tangThuVienPlugin.getTotalChapterFromText(text);
        assertNull(totalChapters);
    }


    @Test
    public void testCalculateTotalPageExactDivision() {
        Integer totalElements = 101;
        Integer numPerPage = 25;
        Integer totalPage = tangThuVienPlugin.calculateTotalPage(totalElements, numPerPage);
        assertEquals(5, totalPage);
    }

    @Test
    public void testCalculateTotalPageZeroElements() {
        Integer totalElements = 0;
        Integer numPerPage = 25;
        Integer totalPage = tangThuVienPlugin.calculateTotalPage(totalElements, numPerPage);
        assertEquals(0, totalPage);
    }

    @Test
    public void testGetChapterIdFromUrl(){
        String url ="https://truyen.tangthuvien.vn/doc-truyen/dao-gia-muon-phi-thang-dao-gia-yeu-phi-thang/chuong-499";
        String expectedChapterId = "chuong-499";
        String chapterId = tangThuVienPlugin.getChapterIdFromUrl(url);
        assertEquals(expectedChapterId, chapterId);
    }

    @Test
    public void testGetAdjacentChaptersOfFirstChapter() throws IOException {
        String storyId = "37669";
        String currentChapterId = "chuong-1";
        String preChapterId = null;
        String nextChapterId = "chuong-2";

        Map<String,String> adjacentChapters = tangThuVienPlugin.getAdjacentChapters(storyId,currentChapterId);

        assertEquals(preChapterId, adjacentChapters.get("preChapter"));
        assertEquals(nextChapterId, adjacentChapters.get("nextChapter"));
    }

    @Test
    public void testGetAdjacentChaptersOfMiddleChapter() throws IOException {
        String storyId = "37669";
        String currentChapterId = "chuong-5";
        String preChapterId = "chuong-4";
        String nextChapterId = "chuong-6";

        Map<String,String> adjacentChapters = tangThuVienPlugin.getAdjacentChapters(storyId,currentChapterId);

        assertEquals(preChapterId, adjacentChapters.get("preChapter"));
        assertEquals(nextChapterId, adjacentChapters.get("nextChapter"));
    }

    @Test
    public void testGetAdjacentChaptersOfLastChapter() throws IOException {
        String storyId = "37669";
        String currentChapterId = "chuong-502";
        String preChapterId = "chuong-501";
        String nextChapterId = null;

        Map<String,String> adjacentChapters = tangThuVienPlugin.getAdjacentChapters(storyId,currentChapterId);

        assertEquals(preChapterId, adjacentChapters.get("preChapter"));
        assertEquals(nextChapterId, adjacentChapters.get("nextChapter"));
    }

    @Test
    public void testGetContentChapter()
    {
        String url = "https://truyen.tangthuvien.vn/doc-truyen/tri-menh-vu-kho/chuong-1";
        String expectedChapterName = "Chương 1 : Man lực như ngưu";
        String expectContent = " 【 nói cố sự nhân, nghe cố sự nhân, cuối cùng đều trở thành trong truyện nhân. 】 <br><br>Hắc sắc hình tượng vén ra một góc. <br><br>Che ở trên mặt vỡ vụn vạt áo bị một con bàn tay bẩn thỉu cấp giật ra. <br><br>Phong đem đầy đất thi thể mùi hôi thối đưa vào người sống cái mũi. <br><br>Lâm Động vô ý thức hít một hơi, xoay người ánh mắt ngưng kết. <br><br>Thi thể! <br><br>Khắp nơi có thể thấy được. <br><br>Không đầu, không có cánh tay, xương vỡ......Quyển lưỡi đao vết đao chỉ lên trời, vết máu loang lổ tinh kỳ trong gió bay phất phới, giữ lại bím tóc, mang theo hồng đầu cân, đều phơi thây nơi này, này là Địa Ngục. <br><br>\"Lại là thanh minh mộng? Cẩu nương dưỡng, tràng diện vẫn còn lớn. \" <br><br>Lâm Động một phát miệng, trên lưng phát lực, một cái lý ngư đả đĩnh dựng đứng lên. <br><br>Những năm này, hắn thường xuyên nằm mơ, làm được đều vẫn là ác mộng, không phải đi đánh nhau, chính là đi hướng đánh nhau trên đường. <br><br>Có đôi khi là xách ống thép đầu đường hoàng mao. <br><br>Có đôi khi là trên nắm tay khỏa vải trắng, đâm đầy pha lê vỡ cặn bã địa hạ quyền thủ. <br><br>Số phận rất nhiều, hỗn đến chính đạo thân phận, đáng tiếc chưa lập công, liền bị một đám con buôn cầm súng bắn chết. <br><br>Ngẫu nhiên vậy có thể khách mời nhất bả đại mạc trong đao khách, đao còn không có che nóng, trời vừa sáng, ký ức liền trở nên mông lung, giống như phiêu hốt đám mây, nhàn nhạt khói nhẹ, trước đây phai màu hình cũ. <br><br>Hắn phủi phủi quần áo, nhìn quanh một vòng, linh giác nhạy cảm. <br><br>Không thích hợp! <br>Lâm Động cúi đầu quét mắt, bàn chân huyết thủy tràn qua, sền sệt lại chân thực. <br><br>Cờ rốp. <br><br>Hắn vặn vẹo uốn éo nắm đấm, xương cốt thanh thúy rung động, hai tay khép lại tóc, chậm rãi về sau bôi, ngẩng đầu, lộ ra một trương tùy ý tùy tiện mặt đến. <br><br>\"Đùa thật ? \" <br><br>Lâm Động mặt mũi tràn đầy ngoan lệ đạo. <br><br>Một người nếu như mỗi ngày bị ác mộng tra tấn, hoặc là thần kinh suy nhược, hoặc là tinh thần nhất định không quá bình thường. <br><br>Lâm Động chính là cái sau. <br><br>Từ lên đại học bắt đầu, cho tới bây giờ, ròng rã bốn năm, có thể thơm ngọt chìm vào giấc ngủ số lần, có thể đếm được trên đầu ngón tay. <br><br>Nhìn qua bác sĩ, mời qua bà đồng đều không hiệu quả gì. <br><br>Về sau vào miếu thắp hương, gặp được một vị pháp sư, cho hắn lấy cái pháp hiệu, gọi là Nguyên Giác—— Lâm Nguyên Giác. <br><br>Mới dần dần kiềm chế tấp nập ác mộng. <br><br>Nhưng cho tới bây giờ không có triệt để tốt qua. <br><br>Lâm Động nhớ kỹ rất rõ ràng, hai ngày trước mụ mụ thay hắn mời về một tôn......Là cái gì tới? <br><br>Nghĩ không ra, có lẽ là Bồ Tát, rất linh nghiệm loại kia. <br><br>Cái này không tựu xuyên việt. <br><br>\"Đây là thượng thiên đáng thương ta, để ta tự tay giải quyết đau đầu căn nguyên. \" <br><br>Lâm Động bản thân an ủi đạo. <br><br>Hắn đại khẩu hút mạnh mấy lần mùi máu tanh, liền thích ứng xuống dưới, đầu tiên là leo đến chỗ cao nhường, sau đó đưa mắt trông về phía xa, sợ hãi trong lòng, càng lúc càng mờ nhạt, ngược lại là diễn sinh ra một loại mãnh liệt phẫn nộ cảm giác. <br><br>Loại tình cảm này cũng không phải là đối với mình cảnh ngộ mà phẫn nộ. <br><br>Mà là đối trên mặt đất những này uổng mạng anh linh. <br><br>Vô luận là hồng đầu cân trường mao, vẫn là bím tóc dài tử Thanh binh đều chỉ là thời đại này bi ai. <br><br>Lọt vào trong tầm mắt là khó mà diễn tả bằng lời thảm liệt. <br><br>Chiến tranh băng lãnh tàn bạo lại vô tình, giữa thiên địa anh linh còn tại gào thét, khói đen quấn xông lên cửu tiêu! <br><br>Mặt trời lặn dư huy, gió lớn từ bắc mà qua. <br><br>Lâm Động đưa tay nắm cái mũi, trong gió đốt cháy khét mùi, huyết tinh mùi, sền sệt như thủy. <br><br>......<br><br>\"Trước thay quần áo khác. \" <br><br>Lâm Động quần áo trên người không hợp nhau. <br><br>Hắn lay mấy lần chung quanh thi thể, tìm cái thể phách cùng mình gần Thanh binh, đem nó trên lưng viết cái lớn chừng cái đấu 'tốt' chữ y phục lột xuống dưới. <br><br>\"Đắc tội, lão huynh, dù không biết ngươi họ gì tên gì, có rảnh tự nhiên là hội cho ngươi đốt chút tiền giấy, đến lúc đó ngươi tìm đến ta lĩnh tiền chính là. \" <br><br>Lâm Động ôm quyền, hỗn bất lận đạo. <br><br>\"Khụ khụ. \" <br><br>Không kịp mảnh cứu xuyên qua nguyên nhân. <br><br>Lâm Động chỉ muốn nhanh lên rời đi nơi này, hắn sở dĩ thay quần áo khác, tự nhiên là để cho mình nhìn qua ở thời đại này, không quá khác người. <br><br>Chính là lúc này. <br><br>Mơ màng tối tăm thiên địa trung, thanh âm huyên náo vang lên. <br><br>Như là bị thực hiện bên trên một đạo định thân ma pháp, Lâm Động ngơ ngẩn, bên tai truyền đến nam nhân khàn khàn tiếng nói. <br><br>【 Thanh triều những năm cuối, yêu ma loạn thế, triều đình mục nát, chinh chiến không ngớt, lương thực không thu hoạch được một hạt nào, bách tính trôi dạt khắp nơi, tiếng kêu than dậy khắp trời đất—— chúng ta, khi cầu sống trong chỗ chết! 】 <br><br>【 Lâm Nguyên Giác, ngươi muốn sống sót, liền nhất định phải dựa theo nhiệm vụ làm việc. 】 <br><br>\"Ngươi là ai......\" <br><br>Lâm Động ở trong lòng hò hét, âm thanh kia trí nhược không nghe thấy. <br><br>【 bổn tràng nhắc nhở:Mã Tân Di là ngươi cơ bản bàn, chiếu cố tốt hắn, cùng hắn thành lập được huynh đệ quan hệ, dù cho chỉ là nhựa tình huynh đệ, tin tưởng ta, ngươi cũng sẽ cần dùng đến. 】 <br><br>【 nhiệm vụ chính tuyến:thu thập bát khổ hạt giống, sinh khổ, lão khổ, đau khổ, chết khổ, oán ghét hận khổ, yêu mà biệt ly khổ, cầu không được khổ, Ngũ Âm hừng hực càng khổ! Thu thập bát khổ hạt giống, hạt giống càng nhiều, cho điểm càng cao. 】 <br><br>【 khiêu chiến nhiệm vụ:? ? ? 】 <br><br>【 thất bại trừng phạt:sự kiện kết thúc, nhiệm vụ chính tuyến cho điểm thấp hơn 60 điểm, thì coi là nhiệm vụ thất bại. Nhiệm vụ thất bại tước đoạt phụ tố, vĩnh viễn dừng lại ở cái thế giới này. 】 <br><br>【 tân thủ phúc lợi một:thế giới hiện tại thân phận. 】<br><br>【 tân thủ phúc lợi hai: man lực như ngưu! （ màu trắng phụ tố）】 <br><br>【 nghe kỹ, ngươi là Lâm Thành Trấn bà con xa chất tử, tên là Lâm Nguyên Giác, ngươi là một cái rất kỳ quái gia hỏa, rõ ràng thể cốt gầy yếu, nhưng lại có gần như một con trâu khí lực. 】 <br><br>【 tại Thanh binh trong đại doanh, ngươi là trời sinh gánh kỳ đạo hãn tốt, hảo hảo lợi dụng, có thể tuyệt đối không được lãng phí thân phận bây giờ còn có thiên phú—— người mở đường. Mahoraka】 <br><br>......<br><br>Bên tai nói dông dài khàn khàn tiếng nói biến mất. <br><br>Hồi lâu sau, Lâm Động lấy lại tinh thần, hack tới sổ? <br><br>Hắc. <br><br>Hắn liếm môi một cái, trong bụng như hỏa thiêu, đột nhiên xuất hiện cảm giác đói bụng, như sóng triều cọ rửa thân thể. <br><br>Lâm Động nắm đấm nắm chặt lại buông ra. <br><br>Hắn có thể rõ ràng cảm nhận được một cỗ ngo ngoe muốn động lực lượng, xương sống lưng cấu kết kinh lạc, từ hông đến vai, qua trụ trời như một đầu vô hình đại long tiềm ẩn trung, mà hắn lấy thân thể gầy yếu đem gánh chịu, đây là trước nay chưa từng có lực lượng. <br><br>Cánh tay nóng lên. <br><br>Một nhóm cổ Tần thời kì màu mực chữ tiểu triện, rõ ràng hiện lên ở trên cổ tay. <br><br>【 man lực như ngưu! 】 <br><br>Khi ngón tay chạm đến hàng chữ này dấu vết thì, kỹ càng nói rõ thông qua ý thức truyền vào não hải. <br><br>【 tên:man lực như ngưu】 <br><br>【 phẩm chất:màu trắng】 <br><br>【 hiệu quả:bị động loại không phải phát động hiệu quả, để nhân tùy thời tùy chỗ có được một con trâu khí lực. 】 <br><br>【 vị trí:tay phải（ trước mắt thanh trang bị đã sử dụng:1/17）】 <br>......<br><br>\"Ngọa tào, đây ý là17 cái ô vuông, mà tính tính toán, tay phải bộ vị, tay trái bộ vị, thủ đoạn, bả vai, đầu, bộ ngực, phần lưng, phần eo, chân, cước bộ, trang sức*2, chiếc nhẫn*2......Nếu là toàn phối trí bên trên phụ tố, chẳng phải là lập tức có thể trở thành siêu anh hùng. \" <br><br>Lâm Động tiêu hóa loại này tin tức rất nhanh, loại trò chơi thiết lập, cơ hồ là nháy mắt giây hiểu. <br><br>Nghĩ đến mỹ hảo tiền đồ, ánh mắt của hắn sáng như rực tinh. <br><br>Cách đó không xa truyền đến động tĩnh. <br><br>Đưa mắt nhìn lại, chẳng lẽ còn có người sống? <br>Đốt cháy thi thể khói đen thăng lên giữa không trung. <br><br>Lượn lờ hơi khói bên trong. <br><br>Một bộ người khoác giáp trụ, dùng cửu hoàn đại đao chống đất thi thể không đầu, lắc lắc ung dung bò lên. Một cái khác nhiễm bùn đất đại thủ, còn đang nắm một viên rách mướp đầu. <br><br>Không giống lắm. <br><br>Nằm binh sĩ, trên thân đồng dạng đều là thuộc da áo vải. <br><br>Lại nhìn một cái bò lên thi thể không đầu, giáp trụ toàn thân lượt sức đồng chất mạ vàng ngâm đinh, tạo thành hình thoi đồ án, có phần như vảy cá, này nhân chiến tử trước đó, nên là cái tướng lĩnh. <br><br>Thô ráp đại thủ, đem đầu lâu nhất bả nhấn tại trên cổ. <br><br>Ken két. <br><br>\"Uy, lão huynh, đầu ngươi trang phản. \" <br><br>Lâm Động rất muốn dạng này nhắc nhở một câu, bất quá lý trí cuối cùng vẫn là nhấn hạ hắn phát tán suy nghĩ. <br><br>Hắn nhìn chằm chằm thi yêu trên ót bím tóc, bước chân lại tại chậm rãi lui lại, động tác biên độ không lớn, lúc đầu ý nghĩ là lui hai bước, co cẳng liền chạy. <br><br>Kết quả gót chân không có mắt, bị thi thể trượt chân. <br><br>\"Thảo\" <br><br>Lâm Động quẳng cái rắn chắc. <br><br>\"Ai? \" <br><br>Trên cổ đầu lâu, vặn ra một cái không thể tưởng tượng độ cong. <br><br>Máu me đầm đìa, diện mục hung ác bím tóc dài tử thi yêu, gắt gao tiếp cận quẳng xuống đất thân ảnh. <br><br>Màu vàng nâu ánh mắt bên trên, lít nha lít nhít bò đầy tơ máu. <br><br>\"Ken két. \" <br><br>\"Giết! \" <br><br>Đưa tay đem đầu lâu bài chính, thi yêu mấy bước vượt qua trên mặt đất hài cốt, huy động đại đao thẳng chặt đến. <br><br>......<br><br>Trái tim tại trong lồng ngực nổi trống, cái này mẹ hắn đều là thứ gì đồ chơi. <br><br>So điện ảnh đều kích thích! <br><br>Đầy đất thi thể, lăn xuống đầu người, trên mặt ngưng kết biểu lộ, truy hồn lấy mạng thi yêu. <br><br>Những này không một không đang khích bác lấy Lâm Động thần kinh. <br><br>\"Có đánh hay không? \" <br><br>Trong lòng của hắn đã có hồi hộp, nhưng đồng dạng dâng lên một loại kích động ý nghĩ, trong thoáng chốc, những cái kia làm qua ác mộng cái này đến cái khác đan xen, phiêu hốt mà đến. <br><br>Giết! Giết! Giết! <br><br>Thi yêu phẫn nộ tiếng rống, ngược lại là kích phát Lâm Động hung tính. <br><br>Mi tâm nhói nhói. <br><br>Trong mộng những cái kia bị hắn giết chết, lại hoặc là giết hắn khuôn mặt, theo nhau mà tới, bọn hắn hét lớn: \"Lâm Nguyên Giác, đao của ngươi đâu? Đao đâu! \" <br><br>Lâm Động quai hàm cắn đến bang gấp, hạ quyết tâm. <br><br>Hắn không biết mình có thể hay không đánh qua thi yêu, bất quá, trong thân thể, mang theo chiến ý nhiệt huyết không ngừng sôi trào. <br><br>Ánh mắt quét qua. <br><br>Hắn chọn lựa một góc chiến trường. <br><br>Đánh khẳng định là muốn đánh. <br><br>Bất quá, đánh như thế nào, hắn định đoạt. <br><br>Vị trí kia tử thi đắp lên nhiều nhất, khói đen quấn, chung quanh tán lạc rách nát binh khí. <br><br>Lâm Động chọn tốt một chút nhi, bước nhanh chạy tới, thi yêu theo sát mà đến. <br><br>Đầu này yêu vật tròng mắt sung huyết, thần sắc tràn đầy bạo ngược, trên mặt da thịt, rữa nát hơn phân nửa. <br><br>Lâm Động bước chân dừng lại, lập tức quay người, hạ thấp đầu vai, nắm lên một cây vết máu pha tạp mâu gãy, xương cột sống ủi thành đại cung, ngay tại hắn muốn hành động thời điểm, bỗng dưng, trong đống người chết nhô ra một cái tay, một phát bắt được thi yêu chân. <br><br>Bịch. <br><br>Thi yêu đập xuống đất, tiếng vang ngột ngạt. <br><br>\"Đoạt đao. \" <br><br>\"Chém đầu. \" <br><br>Đống xác chết dưới đáy truyền ra nam nhân ngột ngạt thanh âm. <br><br>\"Cơ hội tốt. \" <br><br>Bước chân đạp một cái, thân hình nhanh như báo. <br><br>Hắn bắp thịt cả người hở ra, cổ tay phải nóng lên, trên cánh tay gân xanh giống như du động đại long. <br><br>Man lực như ngưu, cái từ này xuyết hiện lên ở trên da diện. <br><br>Lâm Động trên tay đầu mâu, hung hăng cắm vào thi yêu hốc mắt. <br><br>Yêu thi gào thét, miệng rộng nứt đến chân răng, đen nhánh trên đầu lưỡi là pha tạp điểm đỏ, nó còn tại giãy dụa, xú khí huân thiên. <br><br>\"Thảo, cấp gia chết! \" <br><br>Nhất bả chước hạ đại đao, trong khoảnh khắc, Lâm Động giơ tay chém xuống. <br><br>Đinh linh linh, cửu hoàn đao trên thân thiết hoàn, phát ra thanh thúy thanh vang, yêu thi đầu, lăn xuống trên mặt đất, mâu gãy cắm vào một nửa đầu, miệng còn tại khép kín. <br><br>Bỗng nhiên đón thêm một đao. <br><br>Quay đầu. <br><br>Bốn mắt nhìn nhau. <br><br>Trong đống người chết là một trương phẫn uất bi thương mặt. <br><br>Người này nhìn thấy Lâm Động thì, một đôi được hơi mỏng tro mai trong con ngươi, mới thêm ra một tia ánh sáng đến. <br><br>\"Ha ha, trời không tuyệt ta Mã Tân Di con đường, huynh đệ, kéo ta. \" <br><br>Sĩ quan bộ dáng nam nhân, thanh âm rất thấp, khàn khàn, cười thảm bên trong lại lộ ra một tia kiêu hùng mạt lộ ý vị. <br><br>Mã Tân Di? <br>Là hắn đề điểm ta? <br>Lâm Động ngẩn người, trong đầu, đen trắng hình tượng hiện lên. <br><br>Người khoác hắc sắc giáp trụ tướng quân, cưỡi tại đỏ thẫm trên ngựa, suất quân phá vây, một thanh trường đao đùa bỡn hổ hổ sinh uy, đại đao thoáng qua một cái, Thái Bình quân nhân đầu cuồn cuộn. <br><br>......<br><br>Nơi xa. <br><br>Một đội đã quét dọn qua chiến trường Thái Bình quân tướng sĩ, đứng tại lưng chừng núi sườn núi bên trên nhìn quanh. <br><br>Dẫn đầu nhìn thấy hai đạo tương hỗ theo cầm thân ảnh, trùng điệp hừ một tiếng. <br><br>\"Người tới! \" <br><br>Trên tay hắn roi ngựa giương lên. <br><br>\"Bắt bọn hắn lại. \". Được convert bằng TTV Translate. ";

        Map<String,String> contentChapter = tangThuVienPlugin.getContentChapter(url);
        String chapterName = contentChapter.get("chapterName");
        String content = contentChapter.get("content");

        assertEquals(expectedChapterName,chapterName);
        assertEquals(expectContent, content);
    }

    @Test
    public void testMapNovelInfo() throws IOException {
       String novelId = "dao-gia-muon-phi-thang-dao-gia-yeu-phi-thang";
       Map<String, Object> novelInfoMapping = tangThuVienPlugin.mapNovelInfo(novelId);

        String expectedNovelName = "Đạo Gia Muốn Phi Thăng (Đạo Gia Yếu Phi Thăng) - 道爷要飞升";
        Author expectedAuthor = new Author("17689","Bùi Đồ Cẩu");
        Integer expectedTotal = 505;
        String expectedStoryId = "37669";
        String expectedImage = "https://www.nae.vn/ttv/ttv/public/images/story/c492bf066106c16d38b364db00c574bad0d7b62b023a7e9c16a9217334d744cf.jpg";
        String expectedDescription = "Tác phẩm tóm tắt<br>\n “Đệ tử Lê Uyên, bái cầu thanh thiên thụ lục!”<br>\n ……<br>\n ……<br>\n Đã có xong bổn tác phẩm 《 chư giới đệ nhất nhân 》, 《 chư thiên hình chiếu 》《 đại đạo kỷ 》<br>\n ----------------------<br>\n Anh main bởi vì đạt được thụ lục nghi thức của sư phọ để lại mà xuyên qua. Trải qua nghi thức trời xanh thụ cho cái lục ( phù ) đạt được bàn tay vàng Chưởng Binh Lục , từ đó mở ra câu chuyện tại dị giới.<br><br>\n ----------------------<br>\n Nếu mọi người cảm thấy thích và yêu quý converter + hỗ trợ mua text VIP !<br>\n Xin ủng hộ để thêm động lực làm truyện bằng cách : <br>\n Donate MoMo và ViettelPay 0396910852 hoặc Sacombank 030039535269 MAC TU KHOA. Vài đồng cũng đủ mua cốc cafe, hehe.<br>\n Quăng phiếu đề cử để lên xếp hạng !<br>\n --------------------------<br>\n Cảnh giới: Nội kình, tôi thể, nội tráng, dịch hình, Thông Mạch, Luyện Tạng, Luyện Tủy Hoán Huyết, nhập đạo (Địa Sát), Thiên Cương, hợp nhất (đại tông sư).<br>\n Hằng Long đạo, Vạn Long đạo, triều đình: Đại Nhật Kim Lân Chung, Độn Thiên Chu, Phục Ma Long Thần Đao, Trấn Hải Huyền Quy Giáp.<br>\n Hành Sơn đạo Long Hổ tự: Long Hổ Dưỡng Sinh Lô<br>\n Vân Mộng đạo, Trường Hồng kiếm phái: Trường Hồng Nhất Khí Kiếm<br>\n Long Ẩn đạo, Thanh Long các: Nhị sắc Thanh Long giáp<br>\n Cô Tô đạo, Nhất Khí sơn trang: Tam Nguyên Nhất Khí Thung<br>\n Yên Sơn đạo, Tam Muội động: Thiên Hỏa Tam Muội Ấn<br><br>\n (không phải Đạo Tông) Lôi Âm đại châu, Tâm Ý giáo: Thần Long Tu Di Côn<br>\n Trích Tinh lâu: Bát Phương tháp<br>\n Lê Uyên: Liệt Hải Huyền Kình Chùy<br>\n Tần Sư Tiên: Đại Hoang Giá Hải Tử Kim Thương (cơ hồ không ai biết)";
        String expectedFirstChapterId = "chuong-1";

        assertEquals(expectedNovelName, novelInfoMapping.get("novelName"));
        assertEquals(expectedAuthor, novelInfoMapping.get("author"));
        assertEquals(expectedTotal, novelInfoMapping.get("total"));
        assertEquals(expectedStoryId, novelInfoMapping.get("storyId"));
        assertEquals(expectedImage, novelInfoMapping.get("image"));
        assertEquals(expectedDescription, novelInfoMapping.get("description"));
        assertEquals(expectedFirstChapterId, novelInfoMapping.get("firstChapterId"));

    }

    @Test
    public void testGetAllChaptersImpl() throws IOException {
        String storyId = "37669";
        int size = 505;
        List<Chapter> listChapter = tangThuVienPlugin.getAllChaptersImpl(storyId);
        assertEquals(size, listChapter.size());
    }

    @Test
    public void testGetChapterPerPageImpl()
    {
        String storyId = "37669";
        int page = 1;
        int total = 505;
        int expectedSize = 75;

        List<Chapter> chapters = tangThuVienPlugin.getChapterPerPageImpl(storyId,page,total);
        assertEquals(expectedSize, chapters.size());
    }


    @Test
    void testGetNovelDetailSuccess() throws IOException {

        String status = "success";
        String novelName = "Trí Mệnh Vũ Khố - 致命武库";
        String authorName = "Trần Phong Bạo Liệt Tửu";
        String description = "Thu thập phụ tố, xây dựng vô địch vũ khí";
        String firstChapterId = "chuong-1";
        String authorId = "25518";
        String image = "https://www.nae.vn/ttv/ttv/public/images/story/735dd776417121460da98dccf9ea75d824c2f9354eed9dd9bff23f29c563c1d8.jpg";

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelDetail("tri-menh-vu-kho");
        Novel novel = (Novel) response.getData();
        // Assertions
        assertEquals(status, response.getStatus());
        assertEquals(novelName, novel.getName());
        assertEquals(authorName, novel.getAuthor().getName());
        assertEquals(authorId, novel.getAuthor().getAuthorId());
        assertEquals(image, novel.getImage());
        assertEquals(description, novel.getDescription());
        assertEquals(firstChapterId, novel.getFirstChapter());
    }

    @Test
    void testGetNovelDetailError() throws IOException {
        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelDetail("789");
        String expectedStatus = "error";
        // Assertions
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    void testGetNovelChapterDetailSuccess() {
        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelChapterDetail("tri-menh-vu-kho", "chuong-1");

        String status = "success";
        String novelName = "Trí Mệnh Vũ Khố - 致命武库";
        String chapterName = "Chương 1 : Man lực như ngưu";
        String authorName = "Trần Phong Bạo Liệt Tửu";
        String content =  " 【 nói cố sự nhân, nghe cố sự nhân, cuối cùng đều trở thành trong truyện nhân. 】 <br><br>Hắc sắc hình tượng vén ra một góc. <br><br>Che ở trên mặt vỡ vụn vạt áo bị một con bàn tay bẩn thỉu cấp giật ra. <br><br>Phong đem đầy đất thi thể mùi hôi thối đưa vào người sống cái mũi. <br><br>Lâm Động vô ý thức hít một hơi, xoay người ánh mắt ngưng kết. <br><br>Thi thể! <br><br>Khắp nơi có thể thấy được. <br><br>Không đầu, không có cánh tay, xương vỡ......Quyển lưỡi đao vết đao chỉ lên trời, vết máu loang lổ tinh kỳ trong gió bay phất phới, giữ lại bím tóc, mang theo hồng đầu cân, đều phơi thây nơi này, này là Địa Ngục. <br><br>\"Lại là thanh minh mộng? Cẩu nương dưỡng, tràng diện vẫn còn lớn. \" <br><br>Lâm Động một phát miệng, trên lưng phát lực, một cái lý ngư đả đĩnh dựng đứng lên. <br><br>Những năm này, hắn thường xuyên nằm mơ, làm được đều vẫn là ác mộng, không phải đi đánh nhau, chính là đi hướng đánh nhau trên đường. <br><br>Có đôi khi là xách ống thép đầu đường hoàng mao. <br><br>Có đôi khi là trên nắm tay khỏa vải trắng, đâm đầy pha lê vỡ cặn bã địa hạ quyền thủ. <br><br>Số phận rất nhiều, hỗn đến chính đạo thân phận, đáng tiếc chưa lập công, liền bị một đám con buôn cầm súng bắn chết. <br><br>Ngẫu nhiên vậy có thể khách mời nhất bả đại mạc trong đao khách, đao còn không có che nóng, trời vừa sáng, ký ức liền trở nên mông lung, giống như phiêu hốt đám mây, nhàn nhạt khói nhẹ, trước đây phai màu hình cũ. <br><br>Hắn phủi phủi quần áo, nhìn quanh một vòng, linh giác nhạy cảm. <br><br>Không thích hợp! <br>Lâm Động cúi đầu quét mắt, bàn chân huyết thủy tràn qua, sền sệt lại chân thực. <br><br>Cờ rốp. <br><br>Hắn vặn vẹo uốn éo nắm đấm, xương cốt thanh thúy rung động, hai tay khép lại tóc, chậm rãi về sau bôi, ngẩng đầu, lộ ra một trương tùy ý tùy tiện mặt đến. <br><br>\"Đùa thật ? \" <br><br>Lâm Động mặt mũi tràn đầy ngoan lệ đạo. <br><br>Một người nếu như mỗi ngày bị ác mộng tra tấn, hoặc là thần kinh suy nhược, hoặc là tinh thần nhất định không quá bình thường. <br><br>Lâm Động chính là cái sau. <br><br>Từ lên đại học bắt đầu, cho tới bây giờ, ròng rã bốn năm, có thể thơm ngọt chìm vào giấc ngủ số lần, có thể đếm được trên đầu ngón tay. <br><br>Nhìn qua bác sĩ, mời qua bà đồng đều không hiệu quả gì. <br><br>Về sau vào miếu thắp hương, gặp được một vị pháp sư, cho hắn lấy cái pháp hiệu, gọi là Nguyên Giác—— Lâm Nguyên Giác. <br><br>Mới dần dần kiềm chế tấp nập ác mộng. <br><br>Nhưng cho tới bây giờ không có triệt để tốt qua. <br><br>Lâm Động nhớ kỹ rất rõ ràng, hai ngày trước mụ mụ thay hắn mời về một tôn......Là cái gì tới? <br><br>Nghĩ không ra, có lẽ là Bồ Tát, rất linh nghiệm loại kia. <br><br>Cái này không tựu xuyên việt. <br><br>\"Đây là thượng thiên đáng thương ta, để ta tự tay giải quyết đau đầu căn nguyên. \" <br><br>Lâm Động bản thân an ủi đạo. <br><br>Hắn đại khẩu hút mạnh mấy lần mùi máu tanh, liền thích ứng xuống dưới, đầu tiên là leo đến chỗ cao nhường, sau đó đưa mắt trông về phía xa, sợ hãi trong lòng, càng lúc càng mờ nhạt, ngược lại là diễn sinh ra một loại mãnh liệt phẫn nộ cảm giác. <br><br>Loại tình cảm này cũng không phải là đối với mình cảnh ngộ mà phẫn nộ. <br><br>Mà là đối trên mặt đất những này uổng mạng anh linh. <br><br>Vô luận là hồng đầu cân trường mao, vẫn là bím tóc dài tử Thanh binh đều chỉ là thời đại này bi ai. <br><br>Lọt vào trong tầm mắt là khó mà diễn tả bằng lời thảm liệt. <br><br>Chiến tranh băng lãnh tàn bạo lại vô tình, giữa thiên địa anh linh còn tại gào thét, khói đen quấn xông lên cửu tiêu! <br><br>Mặt trời lặn dư huy, gió lớn từ bắc mà qua. <br><br>Lâm Động đưa tay nắm cái mũi, trong gió đốt cháy khét mùi, huyết tinh mùi, sền sệt như thủy. <br><br>......<br><br>\"Trước thay quần áo khác. \" <br><br>Lâm Động quần áo trên người không hợp nhau. <br><br>Hắn lay mấy lần chung quanh thi thể, tìm cái thể phách cùng mình gần Thanh binh, đem nó trên lưng viết cái lớn chừng cái đấu 'tốt' chữ y phục lột xuống dưới. <br><br>\"Đắc tội, lão huynh, dù không biết ngươi họ gì tên gì, có rảnh tự nhiên là hội cho ngươi đốt chút tiền giấy, đến lúc đó ngươi tìm đến ta lĩnh tiền chính là. \" <br><br>Lâm Động ôm quyền, hỗn bất lận đạo. <br><br>\"Khụ khụ. \" <br><br>Không kịp mảnh cứu xuyên qua nguyên nhân. <br><br>Lâm Động chỉ muốn nhanh lên rời đi nơi này, hắn sở dĩ thay quần áo khác, tự nhiên là để cho mình nhìn qua ở thời đại này, không quá khác người. <br><br>Chính là lúc này. <br><br>Mơ màng tối tăm thiên địa trung, thanh âm huyên náo vang lên. <br><br>Như là bị thực hiện bên trên một đạo định thân ma pháp, Lâm Động ngơ ngẩn, bên tai truyền đến nam nhân khàn khàn tiếng nói. <br><br>【 Thanh triều những năm cuối, yêu ma loạn thế, triều đình mục nát, chinh chiến không ngớt, lương thực không thu hoạch được một hạt nào, bách tính trôi dạt khắp nơi, tiếng kêu than dậy khắp trời đất—— chúng ta, khi cầu sống trong chỗ chết! 】 <br><br>【 Lâm Nguyên Giác, ngươi muốn sống sót, liền nhất định phải dựa theo nhiệm vụ làm việc. 】 <br><br>\"Ngươi là ai......\" <br><br>Lâm Động ở trong lòng hò hét, âm thanh kia trí nhược không nghe thấy. <br><br>【 bổn tràng nhắc nhở:Mã Tân Di là ngươi cơ bản bàn, chiếu cố tốt hắn, cùng hắn thành lập được huynh đệ quan hệ, dù cho chỉ là nhựa tình huynh đệ, tin tưởng ta, ngươi cũng sẽ cần dùng đến. 】 <br><br>【 nhiệm vụ chính tuyến:thu thập bát khổ hạt giống, sinh khổ, lão khổ, đau khổ, chết khổ, oán ghét hận khổ, yêu mà biệt ly khổ, cầu không được khổ, Ngũ Âm hừng hực càng khổ! Thu thập bát khổ hạt giống, hạt giống càng nhiều, cho điểm càng cao. 】 <br><br>【 khiêu chiến nhiệm vụ:? ? ? 】 <br><br>【 thất bại trừng phạt:sự kiện kết thúc, nhiệm vụ chính tuyến cho điểm thấp hơn 60 điểm, thì coi là nhiệm vụ thất bại. Nhiệm vụ thất bại tước đoạt phụ tố, vĩnh viễn dừng lại ở cái thế giới này. 】 <br><br>【 tân thủ phúc lợi một:thế giới hiện tại thân phận. 】<br><br>【 tân thủ phúc lợi hai: man lực như ngưu! （ màu trắng phụ tố）】 <br><br>【 nghe kỹ, ngươi là Lâm Thành Trấn bà con xa chất tử, tên là Lâm Nguyên Giác, ngươi là một cái rất kỳ quái gia hỏa, rõ ràng thể cốt gầy yếu, nhưng lại có gần như một con trâu khí lực. 】 <br><br>【 tại Thanh binh trong đại doanh, ngươi là trời sinh gánh kỳ đạo hãn tốt, hảo hảo lợi dụng, có thể tuyệt đối không được lãng phí thân phận bây giờ còn có thiên phú—— người mở đường. Mahoraka】 <br><br>......<br><br>Bên tai nói dông dài khàn khàn tiếng nói biến mất. <br><br>Hồi lâu sau, Lâm Động lấy lại tinh thần, hack tới sổ? <br><br>Hắc. <br><br>Hắn liếm môi một cái, trong bụng như hỏa thiêu, đột nhiên xuất hiện cảm giác đói bụng, như sóng triều cọ rửa thân thể. <br><br>Lâm Động nắm đấm nắm chặt lại buông ra. <br><br>Hắn có thể rõ ràng cảm nhận được một cỗ ngo ngoe muốn động lực lượng, xương sống lưng cấu kết kinh lạc, từ hông đến vai, qua trụ trời như một đầu vô hình đại long tiềm ẩn trung, mà hắn lấy thân thể gầy yếu đem gánh chịu, đây là trước nay chưa từng có lực lượng. <br><br>Cánh tay nóng lên. <br><br>Một nhóm cổ Tần thời kì màu mực chữ tiểu triện, rõ ràng hiện lên ở trên cổ tay. <br><br>【 man lực như ngưu! 】 <br><br>Khi ngón tay chạm đến hàng chữ này dấu vết thì, kỹ càng nói rõ thông qua ý thức truyền vào não hải. <br><br>【 tên:man lực như ngưu】 <br><br>【 phẩm chất:màu trắng】 <br><br>【 hiệu quả:bị động loại không phải phát động hiệu quả, để nhân tùy thời tùy chỗ có được một con trâu khí lực. 】 <br><br>【 vị trí:tay phải（ trước mắt thanh trang bị đã sử dụng:1/17）】 <br>......<br><br>\"Ngọa tào, đây ý là17 cái ô vuông, mà tính tính toán, tay phải bộ vị, tay trái bộ vị, thủ đoạn, bả vai, đầu, bộ ngực, phần lưng, phần eo, chân, cước bộ, trang sức*2, chiếc nhẫn*2......Nếu là toàn phối trí bên trên phụ tố, chẳng phải là lập tức có thể trở thành siêu anh hùng. \" <br><br>Lâm Động tiêu hóa loại này tin tức rất nhanh, loại trò chơi thiết lập, cơ hồ là nháy mắt giây hiểu. <br><br>Nghĩ đến mỹ hảo tiền đồ, ánh mắt của hắn sáng như rực tinh. <br><br>Cách đó không xa truyền đến động tĩnh. <br><br>Đưa mắt nhìn lại, chẳng lẽ còn có người sống? <br>Đốt cháy thi thể khói đen thăng lên giữa không trung. <br><br>Lượn lờ hơi khói bên trong. <br><br>Một bộ người khoác giáp trụ, dùng cửu hoàn đại đao chống đất thi thể không đầu, lắc lắc ung dung bò lên. Một cái khác nhiễm bùn đất đại thủ, còn đang nắm một viên rách mướp đầu. <br><br>Không giống lắm. <br><br>Nằm binh sĩ, trên thân đồng dạng đều là thuộc da áo vải. <br><br>Lại nhìn một cái bò lên thi thể không đầu, giáp trụ toàn thân lượt sức đồng chất mạ vàng ngâm đinh, tạo thành hình thoi đồ án, có phần như vảy cá, này nhân chiến tử trước đó, nên là cái tướng lĩnh. <br><br>Thô ráp đại thủ, đem đầu lâu nhất bả nhấn tại trên cổ. <br><br>Ken két. <br><br>\"Uy, lão huynh, đầu ngươi trang phản. \" <br><br>Lâm Động rất muốn dạng này nhắc nhở một câu, bất quá lý trí cuối cùng vẫn là nhấn hạ hắn phát tán suy nghĩ. <br><br>Hắn nhìn chằm chằm thi yêu trên ót bím tóc, bước chân lại tại chậm rãi lui lại, động tác biên độ không lớn, lúc đầu ý nghĩ là lui hai bước, co cẳng liền chạy. <br><br>Kết quả gót chân không có mắt, bị thi thể trượt chân. <br><br>\"Thảo\" <br><br>Lâm Động quẳng cái rắn chắc. <br><br>\"Ai? \" <br><br>Trên cổ đầu lâu, vặn ra một cái không thể tưởng tượng độ cong. <br><br>Máu me đầm đìa, diện mục hung ác bím tóc dài tử thi yêu, gắt gao tiếp cận quẳng xuống đất thân ảnh. <br><br>Màu vàng nâu ánh mắt bên trên, lít nha lít nhít bò đầy tơ máu. <br><br>\"Ken két. \" <br><br>\"Giết! \" <br><br>Đưa tay đem đầu lâu bài chính, thi yêu mấy bước vượt qua trên mặt đất hài cốt, huy động đại đao thẳng chặt đến. <br><br>......<br><br>Trái tim tại trong lồng ngực nổi trống, cái này mẹ hắn đều là thứ gì đồ chơi. <br><br>So điện ảnh đều kích thích! <br><br>Đầy đất thi thể, lăn xuống đầu người, trên mặt ngưng kết biểu lộ, truy hồn lấy mạng thi yêu. <br><br>Những này không một không đang khích bác lấy Lâm Động thần kinh. <br><br>\"Có đánh hay không? \" <br><br>Trong lòng của hắn đã có hồi hộp, nhưng đồng dạng dâng lên một loại kích động ý nghĩ, trong thoáng chốc, những cái kia làm qua ác mộng cái này đến cái khác đan xen, phiêu hốt mà đến. <br><br>Giết! Giết! Giết! <br><br>Thi yêu phẫn nộ tiếng rống, ngược lại là kích phát Lâm Động hung tính. <br><br>Mi tâm nhói nhói. <br><br>Trong mộng những cái kia bị hắn giết chết, lại hoặc là giết hắn khuôn mặt, theo nhau mà tới, bọn hắn hét lớn: \"Lâm Nguyên Giác, đao của ngươi đâu? Đao đâu! \" <br><br>Lâm Động quai hàm cắn đến bang gấp, hạ quyết tâm. <br><br>Hắn không biết mình có thể hay không đánh qua thi yêu, bất quá, trong thân thể, mang theo chiến ý nhiệt huyết không ngừng sôi trào. <br><br>Ánh mắt quét qua. <br><br>Hắn chọn lựa một góc chiến trường. <br><br>Đánh khẳng định là muốn đánh. <br><br>Bất quá, đánh như thế nào, hắn định đoạt. <br><br>Vị trí kia tử thi đắp lên nhiều nhất, khói đen quấn, chung quanh tán lạc rách nát binh khí. <br><br>Lâm Động chọn tốt một chút nhi, bước nhanh chạy tới, thi yêu theo sát mà đến. <br><br>Đầu này yêu vật tròng mắt sung huyết, thần sắc tràn đầy bạo ngược, trên mặt da thịt, rữa nát hơn phân nửa. <br><br>Lâm Động bước chân dừng lại, lập tức quay người, hạ thấp đầu vai, nắm lên một cây vết máu pha tạp mâu gãy, xương cột sống ủi thành đại cung, ngay tại hắn muốn hành động thời điểm, bỗng dưng, trong đống người chết nhô ra một cái tay, một phát bắt được thi yêu chân. <br><br>Bịch. <br><br>Thi yêu đập xuống đất, tiếng vang ngột ngạt. <br><br>\"Đoạt đao. \" <br><br>\"Chém đầu. \" <br><br>Đống xác chết dưới đáy truyền ra nam nhân ngột ngạt thanh âm. <br><br>\"Cơ hội tốt. \" <br><br>Bước chân đạp một cái, thân hình nhanh như báo. <br><br>Hắn bắp thịt cả người hở ra, cổ tay phải nóng lên, trên cánh tay gân xanh giống như du động đại long. <br><br>Man lực như ngưu, cái từ này xuyết hiện lên ở trên da diện. <br><br>Lâm Động trên tay đầu mâu, hung hăng cắm vào thi yêu hốc mắt. <br><br>Yêu thi gào thét, miệng rộng nứt đến chân răng, đen nhánh trên đầu lưỡi là pha tạp điểm đỏ, nó còn tại giãy dụa, xú khí huân thiên. <br><br>\"Thảo, cấp gia chết! \" <br><br>Nhất bả chước hạ đại đao, trong khoảnh khắc, Lâm Động giơ tay chém xuống. <br><br>Đinh linh linh, cửu hoàn đao trên thân thiết hoàn, phát ra thanh thúy thanh vang, yêu thi đầu, lăn xuống trên mặt đất, mâu gãy cắm vào một nửa đầu, miệng còn tại khép kín. <br><br>Bỗng nhiên đón thêm một đao. <br><br>Quay đầu. <br><br>Bốn mắt nhìn nhau. <br><br>Trong đống người chết là một trương phẫn uất bi thương mặt. <br><br>Người này nhìn thấy Lâm Động thì, một đôi được hơi mỏng tro mai trong con ngươi, mới thêm ra một tia ánh sáng đến. <br><br>\"Ha ha, trời không tuyệt ta Mã Tân Di con đường, huynh đệ, kéo ta. \" <br><br>Sĩ quan bộ dáng nam nhân, thanh âm rất thấp, khàn khàn, cười thảm bên trong lại lộ ra một tia kiêu hùng mạt lộ ý vị. <br><br>Mã Tân Di? <br>Là hắn đề điểm ta? <br>Lâm Động ngẩn người, trong đầu, đen trắng hình tượng hiện lên. <br><br>Người khoác hắc sắc giáp trụ tướng quân, cưỡi tại đỏ thẫm trên ngựa, suất quân phá vây, một thanh trường đao đùa bỡn hổ hổ sinh uy, đại đao thoáng qua một cái, Thái Bình quân nhân đầu cuồn cuộn. <br><br>......<br><br>Nơi xa. <br><br>Một đội đã quét dọn qua chiến trường Thái Bình quân tướng sĩ, đứng tại lưng chừng núi sườn núi bên trên nhìn quanh. <br><br>Dẫn đầu nhìn thấy hai đạo tương hỗ theo cầm thân ảnh, trùng điệp hừ một tiếng. <br><br>\"Người tới! \" <br><br>Trên tay hắn roi ngựa giương lên. <br><br>\"Bắt bọn hắn lại. \". Được convert bằng TTV Translate. ";
        String preChapterId = null;
        String nextChapterId = "chuong-2";

        // Assertions
        assertEquals(status, response.getStatus());
        assertEquals(novelName, ((Chapter) response.getData()).getNovelName());
        assertEquals( chapterName, ((Chapter) response.getData()).getName());
        assertEquals(authorName, ((Chapter) response.getData()).getAuthor().getName());
        assertEquals(content, ((Chapter) response.getData()).getContent());
        assertEquals(preChapterId, ((Chapter) response.getData()).getPreChapterId());
        assertEquals(nextChapterId, ((Chapter) response.getData()).getNextChapterId());
    }

    @Test
    void testGetNovelChapterDetailError() throws IOException {
        String expectedStatus = "error";
        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelChapterDetail("novel123", "chapter789");
        // Assertions
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    void testAuthorDetailSuccess()
    {
        String authorId = "17689";
        String authorName = "Bùi Đồ Cẩu";
        int size = 4;

        DataResponse dataResponse = tangThuVienPlugin.getAuthorDetail(authorId);
        List<Novel> novels = (List<Novel>) dataResponse.getData();

        assertNotNull(dataResponse);
        assertEquals(size, novels.size());
        assertEquals(authorName, novels.get(0).getAuthor().getName());
    }

    @Test
    void testAuthorDetailError()
    {
        String expectedStatus = "error";
        String authorId = "00000";

        DataResponse dataResponse = tangThuVienPlugin.getAuthorDetail(authorId);
        List<Novel> novels = (List<Novel>) dataResponse.getData();

        assertNotNull(dataResponse);
        assertEquals(expectedStatus, dataResponse.getStatus());
    }

    @Test
    void testGetNovelListChaptersPerPageSuccess() {
        String status = "success";
        Integer totalPage = 3;
        Integer currentPage = 1;
        Integer perPage = 75;
        Integer size = 75;

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters("tri-menh-vu-kho",1);

        // Assertions
        assertNotNull(response);
        assertEquals(status, response.getStatus());
        assertEquals(totalPage, response.getTotalPage());
        assertEquals(currentPage, response.getCurrentPage());
        assertEquals(perPage, response.getPerPage());
        assertEquals(size, ((List<Chapter>) response.getData()).size());
    }

    @Test
    void testGetAllNovelListChaptersSuccess() {
        String novelId = "dai-dao-ky-1";
        String expectedStatus = "success";
        int totalPage = 1;
        int currentPage = 1;
        int size = 802;

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId);
        List<Novel> novels = (List<Novel>) response.getData();
        // Assertions
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(totalPage, response.getTotalPage());
        assertEquals(currentPage, response.getCurrentPage());
        assertEquals(size, novels.size());
    }

    @Test
    void testGetAllNovelListChaptersError() {
        String novelId = "dai-dao-ky";

        String expectedStatus = "error";

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId);

        // Assertions
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    void getNovelsPerPageSuccess()
    {
        String expectedStatus = "success";
        int currentPage = 1;
        int totalPage = 599;
        int perPage = 20;
        int size = 20;

        DataResponse dataResponse = tangThuVienPlugin.getAllNovels(1, "");
        List<Novel> novels = (List<Novel>) dataResponse.getData();

        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
        assertEquals(currentPage, dataResponse.getCurrentPage());
        assertEquals(totalPage, dataResponse.getTotalPage());
        assertEquals(perPage, dataResponse.getPerPage());
        assertEquals(size, novels.size());
    }

    @Test
    void getNovelsPerPageError()
    {
        String expectedStatus = "error";

        DataResponse dataResponse = tangThuVienPlugin.getAllNovels(600, "");

        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
    }

    @Test
    void getNovelSearchSuccess()
    {
        String keyword = "kiếm";
        int page = 1;
        String orderBy = "a-z";

        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
        List<Novel> novels = (List<Novel>) dataResponse.getData();

        String expectedStatus = "success";
        int currentPage = 1;
        int totalPage = 27;
        String searchValue = "kiếm";
        int size = 20;

        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
        assertEquals(totalPage, dataResponse.getTotalPage());
        assertEquals(currentPage, dataResponse.getCurrentPage());
        assertEquals(searchValue, dataResponse.getSearchValue());
        assertEquals(size, novels.size());
    }

    @Test
    void getNovelSearchError1()
    {
        String keyword = "kiếm";
        int page = 1000;
        String orderBy = "a-z";

        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);

        String expectedStatus = "error";

        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
    }

    @Test
    void getNovelSearchError2()
    {
        String keyword = "zzzzzzzzzzzzzzzzzz";
        int page = 1000;
        String orderBy = "a-z";

        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);

        String expectedStatus = "error";

        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
    }

    @Test
    void testGetContentChapterSuccess()
    {
        String novelId = "dao-gia-muon-phi-thang-dao-gia-yeu-phi-thang";
        String chapterId = "chuong-1";

        Chapter chapter = tangThuVienPlugin.getContentChapter(novelId, chapterId);

        String chapterName = "Chương 1 : Lê Uyên";
        String content =" Chương 01: Lê Uyên<br><br>\tKhông có<br><br>\tĐại Vận năm 1452, Chập Long phủ, Cao Liễu huyện.<br><br>\tKít xoay ~<br><br>\tLiếc qua sau lưng rối bời giường chung lớn, Lê Uyên đẩy cửa đi ra ngoài, không đợi được cái cuối cùng ra khỏi phòng.<br><br>\tCuối thu rạng sáng sương mù còn chưa đều tán đi, Cao Liễu huyện thành nội đã là dâng lên từng sợi khói bếp, trong lúc mơ hồ, có thể nghe tới một số người thanh.<br><br>\tĐập vào mi mắt, là một gian không lớn tiểu viện, sáu bảy cùng hắn đồng dạng lớn nhỏ, thân mang áo gai, mười lăm mười sáu tuổi thiếu niên đang bận rộn.<br><br>\tRửa mặt, chẻ củi, múc nước, nhóm lửa, nấu cơm. . .<br><br>\t\"Hô!\"<br><br>\tNắm thật chặt đơn bạc màu xám áo gai, Lê Uyên hít sâu một hơi, tại mấy cái học đồ la lên trung gia nhập vào.<br><br>\tLàm 'Rèn Binh cửa hàng' học đồ một ngày, từ chẻ củi múc nước bắt đầu.<br><br>\t\"Mới một tháng lẻ ba ngày, mười hai năm làm sao chịu? Đây cũng quá khó. . . Đời trước học ít đồ tựu không dễ dàng, đời này càng khó!\"<br><br>\tTay chân lanh lẹ bận rộn, Lê Uyên nhịn không được trong lòng thở dài.<br><br>\tĐời trước cùng nhà mình dã đạo sư phụ học tu núi lập mộ phần, siêu độ pháp sự, khó sinh trợ sinh kia một bộ, hắn cũng bất quá dùng hai ba năm mà thôi.<br><br>\tCái này Rèn Binh cửa hàng' học đồ trước sau thế mà muốn mười hai năm!<br><br>\tBa năm làm việc vặt, hai năm làm giúp, bảy năm hiệu lực!<br><br>\tLê Uyên trong lòng oán thầm, trên tay cũng không dám chậm nửa phần, đành phải thở dài mình 'Thức tỉnh' muộn, ngủ một giấc tỉnh, đã 'Bán mình' cho 'Rèn Binh cửa hàng' .<br><br>\tCòn bởi vì thân thể quá mức gầy yếu không có phân đến tiền viện. . .<br><br>\t\"Phanh!\"<br><br>\tĐột nhiên, phòng bếp chỗ truyền đến một tiếng vang trầm.<br><br>\tTay cầm muôi lớn mập mạp một cước đem một cái áo xám học đồ đạp lăn trên mặt đất, trên mặt dữ tợn vung vẩy:<br><br>\t\"Cẩu nương dưỡng oắt con, lão tử học chiêu này xóc chảo làm đồ ăn bản sự ăn không biết bao nhiêu khổ, ngươi cũng dám học trộm? !\"<br><br>\t\"Tôn cầm muôi, ta sai, cũng không dám lại, không dám. . .\"<br><br>\tKia học đồ ôm đầu kêu rên, nhưng cũng không dám tránh né, đối cứng ai đó một trận đánh đập.<br><br>\tTrong tiểu viện một đám học đồ câm như hến, Lê Uyên cúi đầu, sắc mặt đờ đẫn.<br><br>\tHọc đồ phạm sai lầm, đám thợ cả tự nhiên là có thể tùy ý đánh chửi, đây là viết tại 'Văn tự bán mình' bên trong.<br><br>\t\"Đồ đệ đồ đệ, ba năm nô lệ\", lời này cũng không phải nói một chút mà thôi.<br><br>\tKia xóc chảo Tôn mập mạp cũng coi là nửa cái sư phó, trông coi bọn hắn mười cái học đồ, cũng trông coi Rèn Binh cửa hàng trên dưới một trăm người ăn uống, tính tình ác liệt, đánh chửi học đồ tự nhiên là chuyện thường ngày.<br><br>\tMột tháng trước vừa tới thời điểm, Lê Uyên thiếu chút nữa cũng bị kia muôi cơm gõ vỡ đầu. . .<br><br>\t\"Tay chân đều nhanh nhẹn lấy điểm, tiền viện đám thợ cả nhanh dậy, trì hoãn bọn hắn ăn cơm, coi như không phải chịu mấy cước sự tình!\"<br><br>\tKia Tôn mập mạp ước lượng muôi, hùng hùng hổ hổ.<br><br>\tMột đám học đồ không dám ngẩng đầu, tay chân lại là càng phát ra nhanh thêm mấy phần.<br><br>\tThẳng đến mập mạp này lại trở về phòng bếp, kia bị đánh học đồ mới không rên một tiếng bò lên, dù là toàn thân kịch liệt đau nhức, nhưng cũng không dám trì hoãn làm việc.<br><br>\tLê Uyên thần sắc đờ đẫn.<br><br>\tRèn Binh cửa hàng là Cao Liễu huyện thành lớn nhất tiệm thợ rèn một trong, nuôi hộ vệ, sư phó, làm giúp, học đồ trăm tám mươi người.<br><br>\tHọc đồ, tự nhiên là địa vị thấp nhất, tiền công ăn ít kém không nói, còn động một tí bị đánh bị mắng, muốn đãi ngộ rất nhiều, ít nhất phải nấu thành làm giúp, thậm chí sư phó.<br><br>\t\"Mười hai năm a. . .\"<br><br>\tKhi bốn vòng đại nhật tại biển mây ở giữa dâng lên lúc, đồ ăn cũng cuối cùng là làm tốt, một đám bận rộn học đồ trên mặt cũng có chút tiếu dung.<br><br>\tTrừ ăn cơm thời gian bên ngoài, đám học đồ suốt ngày cũng thật là không có cái gì nhàn rỗi.<br><br>\tChẻ củi, gánh nước, chuẩn bị than củi, vận chuyển các loại thỏi sắt, lau binh khí, quét dọn. . .<br><br>\tNhưng cho dù dạng này, Rèn Binh cửa hàng mỗi lần chiêu học đồ cũng không thiếu người tới.<br><br>\tBởi vì, Rèn Binh cửa hàng đãi ngộ là Cao Liễu huyện tuyển nhận học đồ các trong nhà đãi ngộ tốt nhất.<br><br>\tChẳng những mỗi tháng có ba mươi tiền đồng tiền công, cơm nước cũng so với một đám tiệm bán thuốc, thợ mộc trải loại hình muốn tốt rất nhiều.<br><br>\tRèn sắt thế nhưng là mười phần vất vả công việc, trong bụng chưa hàng có thể làm không được cái này.<br><br>\tĐương nhiên, học đồ nhưng không có đãi ngộ này, mỗi bảy ngày, mới có thể thấy chút dầu nước, tựu cái này, một đám học đồ cũng đã mười phần thỏa mãn.<br><br>\tBên cạnh mấy nhà, thế nhưng chỉ có tại mùng một, mười lăm mới có thể nhìn thấy một điểm thức ăn mặn, càng có, quanh năm suốt tháng đều thấy không được nửa điểm chất béo. . .<br><br>\tMặt trời lên cao, tiền viện đều mơ hồ có thể nghe tới 'Đinh đinh đang đang' rèn sắt thanh lúc, một đám học đồ vừa mới ăn vào điểm tâm, có một lát thời gian nghỉ ngơi.<br><br>\t\"Chịu ba năm mới có thể trở thành làm giúp. . .\"<br><br>\tĐám học đồ tốp năm tốp ba ngồi xổm ở góc tường, Lê Uyên bưng bát cơm ngồi xổm ở một góc, hai vai bủn rủn cầm bánh ngô, trong lòng không khỏi có chút chua xót.<br><br>\tKiếp trước, hắn mặc dù là cái không vào biên, không nhập đạo tịch đạo sĩ dởm, nhưng đi theo sư phụ trà trộn nông thôn ở giữa cũng hơi có chút chất béo có thể kiếm, liền xem như kém cỏi nhất thời điểm, cũng không có như vậy thê thảm.<br><br>\tGiờ khắc này, hắn lại nhịn không được hoài niệm khởi kiếp trước.<br><br>\tBia, đồ uống tiểu đồ nướng, điều hoà không khí TV ghế sô pha điện thoại. . . Thậm chí là nhà mình cái kia trước khi chết đều nhớ mãi không quên nhập biên nhập đạo tịch sư phụ.<br><br>\t\"Làm sao liền xuyên qua rồi? ! Ta còn có thể trở về sao? !\"<br><br>\tLiền nước cháo nuốt xuống bánh ngô, Lê Uyên ngửa đầu liếc mắt nhìn trên trời một lớn ba nhỏ, bốn vòng mặt trời đỏ, kém chút nước mắt chảy ròng.<br><br>\tLàm sao liền xuyên qua rồi?<br><br>\tQuá khứ trong vòng hơn một tháng, Lê Uyên không chỉ một lần nghĩ tới vấn đề này, từng lần một nhớ lại mình xuyên qua trước ký ức.<br><br>\tThân là một cái không vào biên đạo sĩ dởm, nhà mình sư phụ sau khi đi, hắn liền trong hương thôn cưới tang gả cưới đều nhanh vớt không được, bất đắc dĩ đổi nghề, tại quê quán huyện thành mở nhà tiểu siêu thị, dù không có tiền, nhưng cũng thanh tĩnh.<br><br>\t\"Ngày đó ta uốn tại ghế sô pha bên trong chơi điện thoại, sau đó . . . chờ một chút, thụ lục nghi thức!\"<br><br>\tNhư có thiểm điện vạch phá mê vụ, Lê Uyên toàn thân run lên, kém chút đem nửa bát nước cháo đều hất tới trên mặt đất!<br><br>\t\"Là, thụ lục nghi thức!\"<br><br>\tCưỡng chế lấy trong lòng rung động, Lê Uyên một lần nữa ngồi xổm trở về, không có dẫn tới người khác chú ý, tâm tư lại không thể ức chế phát tán.<br><br>\tKiếp trước, hắn kia đến chết cũng không thể nhập biên sư phụ, từng để lại cho hắn nửa cuốn đạo thư.<br><br>\tCái kia đạo thư, hắn nhìn qua mấy lần, nhưng cũng không quá cảm thấy hứng thú, đối với nhà mình sư phụ khi còn sống nhớ mãi không quên 'Thụ lục thành tiên' cũng căn bản xem thường.<br><br>\tThẳng đến xuyên qua trước ngày ấy, hắn cũng không biết làm sao liền nhớ lại kia trên sách ghi chép nghi thức.<br><br>\tSau đó tựu. . .<br><br>\t\"Đến cùng phải hay không bởi vì kia nghi thức?\"<br><br>\tĐem nửa cái bánh ngô tính cả nửa bát nước cháo đều rót vào trong bụng, Lê Uyên trong lòng vẫn là lẩm bẩm.<br><br>\tHắn không cho rằng kia cái gì thụ lục nghi thức có thể dẫn đến mình xuyên qua, nhưng trừ cái này, hắn thật là nghĩ không ra những khả năng khác.<br><br>\t\"Thụ lục, thương thiên thụ lục. . .\"<br><br>\tLê Uyên trong lòng lầm bầm.<br><br>\tThụ lục, hắn tự nhiên không xa lạ gì.<br><br>\tĐạo gia cho rằng phù là thiên địa tự nhiên kết thành, từ thần tiên mô tả, truyền hậu thế ở giữa.<br><br>\tMà đạo sĩ chỉ có trao nhận phù lục về sau, tên trèo lên thiên tào, có đạo vị thần chức phía sau, mới có thể điều khiển phù lục, dùng cái này có được triệu hặc quỷ thần, an trấn ngũ phương, hàng yêu trấn ma, chữa bệnh trừ tai chờ không thể tưởng tượng nổi đạo thuật thần thông.<br><br>\t\"Lão đầu tử cả đời nhớ mãi không quên 'Thụ lục thành tiên', chẳng lẽ sẽ là thật sao?\"<br><br>\tLê Uyên trong lòng thì thào.<br><br>\tLão đầu tử chấp niệm, hắn vốn là không tin, cái gì năm tháng, còn tin thụ lục thành tiên kia một bộ.<br><br>\tHắn kiếp trước cũng nhận ra mấy cái thụ lục đạo nhân, cũng không gặp ai có đạo thuật gì thần thông.<br><br>\tNhất là, kia nửa cuốn đạo thư bên trên ghi chép còn không phải người chính thống thụ lục.<br><br>\tPhải biết, chính thống thụ lục nghi thức, chẳng những muốn thụ lục, còn bao gồm trải qua, giới, muốn truyền độ, còn phải có lục đàn giám độ sư, truyền độ sư, tiến cử hiền tài sư vân vân.<br><br>\tMà cái kia đạo thư bên trên cái gọi là 'Thương thiên thụ lục', là thật là đơn giản, thậm chí có thể nói đơn sơ. . .<br><br>\tNhưng bây giờ. . .<br><br>\t\"Ta đều xuyên qua, còn có cái gì không có khả năng? Mặc dù nhưng là. . . Nhưng vạn nhất đâu?\"<br><br>\tLê Uyên tâm tư phát tán.<br><br>\tLý tính nói cho hắn, chuyện này không có khả năng lắm, nhưng trong lòng vẫn là ôm lấy may mắn,<br><br>\tVạn nhất đâu?<br><br>\t\"Vạn nhất ta, không, Đạo gia ta thật có thụ lục thành tiên cơ duyên đâu?\"<br><br>\tCàng nghĩ, Lê Uyên trong lòng càng là rung động khó đè nén, nếu không phải biết được bây giờ thời gian không đúng, hận không thể lập tức đi cử hành nghi thức.<br><br>\tCái gì gặp quỷ học đồ, làm giúp, sư phó, chưởng quỹ?<br><br>\tĐạo gia muốn về nhà!<br><br>\tĐạo gia muốn thành tiên! ";

        // Assertions
        assertNotNull(chapter);
        assertEquals(chapterName, chapter.getName());
        assertEquals(content, chapter.getContent());
    }

    @Test
    void testGetContentChapterError1()
    {
        String novelId = "chapterId";
        String chapterId = "chuong-1";

        Chapter chapter = tangThuVienPlugin.getContentChapter(novelId, chapterId);

        // Assertions
        assertNotNull(chapter);
        assertEquals("", chapter.getName());
        assertEquals("", chapter.getContent());
    }

    @Test
    void testGetContentChapterError2()
    {
        String novelId = "dao-gia-muon-phi-thang-dao-gia-yeu-phi-thang";
        String chapterId = "chapter1";

        Chapter chapter = tangThuVienPlugin.getContentChapter(novelId, chapterId);

        // Assertions
        assertNotNull(chapter);
        assertEquals("", chapter.getName());
        assertEquals("", chapter.getContent());
    }
}
