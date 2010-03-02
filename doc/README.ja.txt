GroovyServer 0.1 README
2010�N03��02��

==========
�͂��߂�
==========

GroovyServer�́AGroovy�����n���T�[�o�[�Ƃ��ē��삳���邱�Ƃ�groovy�R�}
���h�̋N���������ڍ�����������̂ł��Bgroovy�R�}���h��TCP/IP �ʐM���g��
�Ă��łɋN�����Ă���Groovy�����^�C���ƒʐM���A���ʂ��o�͂��܂��B

�����AEmacs��emacsserver/Emacsclient(��������gnuserver/gnuclient)������
�m�Ȃ�Η����������ł��傤�B

Groovy�X�N���v�g���J������ꍇ�A�N�����x���ƂĂ��d�v�ł��BGroovy�͓��I
����ł��邽�߁A�R���p�C�����ɂ��炩���߃`�F�b�N�ł��Ȃ��G���[�ɂ��āA
���s���ď��߂Ĕ�������ꍇ����������ł��B���̂��߁A���s���J��Ԃ��Ȃ�
��J�������Ă������ƂɂȂ�܂��B���Ƃ����̋N����1..2�b����������Ȃ���
���A�̊��Ƃ��Ă͂ƂĂ��傫����������̂ł͂Ȃ��ł��傤���B

GroovyServer���g�����ƂŁA�N�����Ԃ�Z�k���A���������ƊJ����i�߂Ă���
���Ƃ��ł��܂��B�ȉ��́AWindows XP Core(TM) 2 Duo 2GHz�̃}�V���ł̋N��
���Ԃ̗�ł�(�O�񑪒肵�����ϒl�j�B

Groovy�R�}���h(��native��) 2.32 (sec)
Groovy�R�}���h(native��)   0.90 (sec)
GroovyClient               0.10 (sec)

��nateve�łƔ�ׂ�Ɩ�20�{���x�̋N�������������Ă��܂��B

==========
�����
==========

����m�F���s���Ă�����͈ȉ��̂Ƃ���B

- Linux
- Windows cygwin
- MacOS X 10.5/6

================
Groovy�̃o�[�W����
================

Groovy 1.8-SNAPSHOT, 1.7,1.6.�œ���m�F���s���Ă��܂��B

============================================
�o�C�i���p�b�P�[�W����̃C���X�g�[��
============================================

�o�C�i���p�b�P�[�Wgroovyserv-0.1-SNAPSHOT-win32-bin.zip��K���ȃt�H���_
�ɓW�J���܂��B�Ⴆ�΁A~/opt�ɓW�J����Ƃ��܂��B

 > mkdir ~/opt
 > cd ~/opt
 > unzip -l groovyserv-0.1-SNAPSHOT-win32-bin.zip

�ȍ~�A�W�J�����f�B���N�g�����ȍ~GROOVYSERV_HOME�ƌĂт܂��B���Ɋ��ϐ�
PATH��GROOVYSERV_HOME/bin��ǉ����܂��B���ɁA/opt/groovyserv�ɓW�J����
�ꍇ�A�ȉ��̂悤�ɐݒ肵�܂�(bash�Ȃǂ̐ݒ�)�B

 export PATH=~/opt/groovyserv-0.1-SNAPSHOT/bin:$PATH

�ݒ�͈ȏ�ł��Bgroovyclient�����s�����groovyserver���N�����܂��B

 > groovyclient -v
 starting server..
 Groovy Version: 1.7.0 JVM: 1.6.0_13


============================================
�\�[�X�R�[�h����̃r���h
============================================

�܂��AGroovyServer�̃\�[�X�R�[�h�z�z�p�b�P�[�W
groovyserv-0.1-SNAPSHOT-src.zip*��W�J���܂��B

 > mkdir -p ~/opt/src
 > cd ~/opt/src
 > unzip -l groovyserv-0.1-SNAPSHOT-src.zip

Maven2���g���ăR���p�C�����܂��B

 > cd ~/opt/src/groovyserv-0.1-SNAPSHOT/
 > mvn clean compile

�R���p�C���������ʁALinux,Mac OS X���ł�

  ~/opt/src/groovyserv-0.1-SNAPSHOT/bin/groovyclient

���Acygwin/Windows���ł�

  ~/opt/src/groovyserv-0.1-SNAPSHOT/bin/groovyclient.exe

���ł���Ă���΃R���p�C���ɐ������Ă��܂��B�e�X�g�Ŏ��s����ꍇ�ȉ���
�悤�ɂ��Ă��������B

 > mvn -Dmaven.test.skip=true clean compile

�o�C�i���p�b�P�[�W����̃C���X�g�[���̏ꍇ�Ɠ����悤�Ɋ��ϐ�PATH���
�肵�Ă��������B

===============
���̑��̐ݒ�
===============

groovy�R�}���h�����s�����groovyclient�R�}���h���Ăяo�����悤�ɁA��
���̂悤�ɃG�C���A�X(�ʖ�)�w����s���Ă����ƕ֗��ł��B�ȉ���bash�p�̃G
�C���A�X�̐ݒ�ł��B

  alias groovy=groovyclient
  alias groovyc="groovyclient -e 'org.codehaus.groovy.tools.FileSystemCompiler.main(args)'"
  alias groovysh="groovyclient -e 'groovy.ui.InteractiveShell.main(args)'"
  alias groovyConsole="groovyclient -e 'groovy.ui.Console.main(args)'"

================
�g����
================

groovy�R�}���h�̑����groovyclient�R�}���h�����s���܂��Bgroovyclient
�����s�����Ƃ��Agroovyserver���N������Ă��Ȃ���΁A�o�b�N�O���E���h��
groovyserver���N������܂��B�N������Ă��Ȃ��ꍇ�A�N���̂��߂ɐ��b�̑�
�����Ԃ̌�A�T�[�o���N�����A���s���s���܂��B

�����I��groovyserver���N�����Ă������Ƃ��ł��܂��B

 > groovyserver

�N���I�v�V������-v���w�肷��Əڍ׃��b�Z�[�W���\������܂��B

 > groovyserver

���ł�groovyserver���N�����Ă���ꍇ�A�G���[���b�Z�[�W���\������܂��B
���̏ꍇ�A�ȉ��̂悤�ɏI�������Ď��s���邩

 > groovyserver -k
 > groovyserver

-r�I�v�V�������g���čċN�����邱�Ƃ��ł��܂��B

 > groovyserver -r

================
�����E�@�\�̈Ⴂ
================

* �قȂ�J�����g�f�B���N�g���𓯎��Ɏg�����Ƃ͂ł��܂���B���Ƃ��΁A�p
  �C�v�łȂ��łQ��Groovy�R�}���h�����s���A���ꂼ�ꂪ�قȂ�J�����g
  �f�B���N�g���ł���悤�Ɏ��s���邱�Ƃ͂ł��܂���B

   >  groovy -e "..."   | (cd /tmp; groovy -e "......") 

  ���̏ꍇ��O���������܂��B

  org.jggug.kobo.groovyserv.GroovyServerException: Can't change
  current directory because of another session running on different
  dir: ....

  �����̃R���\�[��������s�����ꍇ�ŁA���ꂼ��̃R���\�[���ňقȂ�J��
  ���g�f�B���N�g���Ŏ��s�����ꍇ�������ł��B�����Ɏ��s���ɂȂ邱�Ƃ��Ȃ�
  �Ă� ����΁A�قȂ�J�����g�f�B���N�g���ŁA�����̃R���\�[�����痘�p��
  �Ă���肠��܂���B

* �ÓI�ϐ���groovy�v���O�����Ԃ̎��s�ŋ��L����܂��B���Ƃ��΁A�V�X�e��
  �v���p�e�B�����L����܂��B

  > groovyclient -e "System.setProperty('a','abc')"
  > groovyclient -e "println System.getProperty('a')"
  abc

* ���ϐ��ɂ��āAgroovyclient�R�}���h�����s�����Ƃ��̃V�F���̏�Ԃ�
  �͂Ȃ��groovyserver�����s���ꂽ�Ƃ��̊��ϐ��̒l���g�p����܂��B��
  �ϐ��̕ύX��groovy�R�}���h�ɉe�����y�ڂ��悤�ɂ��邽�߂ɂ́A
  groovyserver���ċN������K�v������܂��B���������ϐ�CLASSPATH�̎w��
  �̓N���C�A���g�T�C�h�̂��̂����I�ɐݒ肳��܂��B

* groovyclient���s���̊��ϐ�CLASSPATH�l�A�����-cp�I�v�V�����Őݒ肵
  ���N���X�o�X���͓��I�ɃT�[�o���̃N���X�p�X�ɒǉ�����܂��B�������A
  ���̂悤�ȓ��I�ȃN���X�p�X���͒ǉ��݂̂��Ȃ���A�폜����邱�Ƃ͂�
  ��܂���B


================
�I�v�V����
================

TCP�̃|�[�g�ԍ��̓f�t�H���g�ł�1961���g�p���܂��B�ύX����ɂ�[TBD]
GroovyServer�̋N���I�v�V�����͈ȉ��̂Ƃ���B

   -v  �璷�\���B�f�o�b�O���Ȃǂ�\�����܂��B
   -k �N�����Ă���GroovyServer���I�������܂��B
   -r �N�����Ă���GroovyServer���ċN�����܂�(��~+�N��)�B
   -p <port> GroovyServer���N���C�A���g�Ƃ̒ʐM�Ɏg�p����
             �|�[�g�ԍ����w�肵�܂��B

================
�|�[�g�ԍ�
================

�ʐM�Ɏg�p����|�[�g�ԍ���ύX����ɂ́A���ϐ�GROOVYSERVER_PORT��ݒ�
���Ă��������B�N���C�A���g�ł�-p�I�v�V�����Őݒ肷�邱�Ƃ��ł��܂��B-p
�I�v�V�����̐ݒ�͊��ϐ��̐ݒ�ɗD�悳��܂��B

================
���O�t�@�C��
================

~/.groovy/groovyserver/<�v���Z�XID>.log

��groovyserver�̃��O�t�@�C�����o�͂���܂��B

================
�Z�L�����e�B
================

GroovyServer�ւ̐ڑ��́Alocalhost����݂̂ɐ�������Ă���A���̃}�V����
���groovyclient�R�}���h���g���Đڑ����邱�Ƃ͂ł��܂���B�܂��A���}�V
����ł�groovyserver���N���������[�U�[�Ɠ������[�U���s����groovyclient
�̐ڑ������󂯕t���Ȃ��悤�ɐ���������Ă��܂��B�Ȃ��A������́A�T�[�o
�����s��Ƃɐ�������閧�̃N�b�L�[���~/.groovy/groovyserv/key �t�@�C��
�̓��e�𓯂����[�U�݂̂��ǂݏo���邱�ƂɈˑ����Ă��܂��B���̃t�@�C����
�A�N�Z�X�������AUNIX���ł�owner�ȊO����͓ǂ߂Ȃ��悤�ɐݒ�(chmod
400)���Ă��܂����AWindows�ł͂��̐ݒ肪�@�\���Ȃ����߁A���t�@�C����
���̃��[�U����ǂݏo���ꂤ��ꍇ�ɂ͒��ӂ��Ă��������B

================
�A����
================

- github��URL

================
�N���W�b�g
================

- Kobo Project.
- NTT Software Corp.
