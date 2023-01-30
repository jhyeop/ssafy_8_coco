import styled from "styled-components";

import ParticipantsInfoBar from "./ParticipantsInfoBar";
import ChatList from "./ChatList";
import ChatInput from "./ChatInput";

const ChatAreaDiv = styled.div`
  box-sizing: border-box;
  background-color: #4A4E69;
  display: flex;
  flex-direction: column;
  flex: 7;
  width: 100%;
`;

function ChatArea(props) {
  return (
    <ChatAreaDiv>
      {/* 참여자 정보 인퍼페이스 */}
      <ParticipantsInfoBar />
      {/* 메세지 컨텐츠 칸 */}
      <ChatList />
      {/* 메세지 입력 칸 */}
      <ChatInput />
    </ChatAreaDiv>
  );
}

export default ChatArea;