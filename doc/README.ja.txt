GroovyServer README

==========
�͂��߂�
==========

GroovyServer�́AGroovy�C���^�v���^���풓�����T�[�o�[�Ƃ��ē��삳���邱
�Ƃ�groovy�R�}���h�̋N����������������̂ł��B

�����AEmacs��emacsserver/Emacsclient(��������gnuserver/gnuclient)������
�m�Ȃ�Η����������ł��傤�B

groovy�ŃX�N���v�g���g�p����ꍇ�A�N���̃��X�|���X���g������ɑ傫�ȉe
�����y�ڂ��܂��B�Ȃ��Ȃ�AGroovy�͎��s���̌^�`�F�b�N���s������ł����A
�R���p�C�����̌^�`�F�b�N���ł��Ȃ��̂ŁA���s����𔺂����s���J��Ԃ���
�ƂŌ^�G���[���͂��߂Č��o�ł��邩��ł��B�K�R�I�ɁA�Ȃ�ǂ����s���J��
�Ԃ����ƂɂȂ�܂����AJava�ł��邽�߁A�N�����Ԃ�����������܂��B

groovyserver���g�����ƂŁA�N�����Ԃ̒x�����ŏ����ɂ��邱�Ƃ��ł�������
���J�����s�����Ƃ��ł���ł��傤�B


==========
�����
==========

UNIX�������Windows��cygwin���œ��삵�܂��BUNIX���ɂ��Ă�MacOS
X�œ���m�F���s���Ă��܂��B

================
Groovy�̃o�[�W����
================

1.7-RC-1�A1.6.6�ȏ�œ���m�F���s���Ă��܂��B

============================================
�\�[�X�R�[�h�ł̔z�z�p�b�P�[�W����̃C���X�g�[��
============================================

�܂��AGroovyServer�z�z�p�b�P�[�W��W�J���܂��B�ȍ~�ł́AGroovyServer�z
�z�p�b�P�[�W��W�J�����f�B���N�g�����u$GROOVYSERV_HOME�v�ƕ\�L���܂��B

 $ unzip groovyserv-1.x.x.zip

maven���g���ăR���p�C�����܂��B

 $ cd $GROOVYSERV_HOME
 $ mvn compile

�R���p�C���������ʁALinux,Mac OS X���ł�

 $GROOVYSERV_HOME/bin/groovyclient

���ł���Ă���΃R���p�C���ɐ������Ă��܂��B
cygwin Windows���ł͐��������̂�

 $GROOVYSERV_HOME/bin/groovyclient.exe

�ł��B���ɁA���ϐ�PATH�Ɂu$GROOVYSERV_HOME/bin�v���܂܂��悤�ɕύX���܂��B

  export PATH=$GROOVYSERV_HOME/bin:$PATH

�ȉ��̂悤�ɃR�}���h���C������ł�����Ŋm�F���Ă݂Ă��������B

 $ groovyclient -v

�܂��Agroovy�R�}���h�����s�����groovyclient�R�}���h���Ăяo�����悤�ɁA
�ȉ��̂悤�ɃG�C���A�X(�ʖ�)�w����s���Ă����ƕ֗��ł��B
�ȉ���bash�p�̃G�C���A�X�̐ݒ�ł��B

  alias groovy=groovyclient
  alias groovyc="groovyclient -e 'org.codehaus.groovy.tools.FileSystemCompiler.main(args)'"
  alias groovysh="groovyclient -e 'groovy.ui.InteractiveShell.main(args)'"
  alias groovyConsole="groovyclient -e 'groovy.ui.Console.main(args)'"


======================
�o�C�i���p�b�P�[�W
======================

 [TBD]

================
�g����
================

groovy�R�}���h�̑����groovyclient�R�}���h�����s���܂��B�O�q�̂悤��
�G�C���A�X�w����s���Ă���΁A�G�C���A�X�ł���groovy�R�}���h���Agroovy
�R�}���h�Ǝv���Ď��s����Ηǂ��ł��B

groovyclient�����s�����Ƃ��Agroovyserver���N������Ă��Ȃ���΁A�o�b�N
�O���E���h��groovyserver���N������܂��B�N������Ă��Ȃ��ꍇ�A�N���̂�
�߂ɐ��b�̑҂����Ԃ̌�A�T�[�o���N�����A���s���s���܂��B

 $ groovyclient -v

================
�����E�@�\�̈Ⴂ
================

* �قȂ�J�����g�f�B���N�g���𓯎��Ɏg�����Ƃ͂ł��܂���B���Ƃ��΁A�p
  �C�v�łȂ��łQ��Groovy�R�}���h�����s���A���ꂼ�ꂪ�قȂ�J�����g
  �f�B���N�g���ł���悤�Ɏ��s���邱�Ƃ͂ł��܂���B

   $  groovy -e "..."   | (cd /tmp; groovy -e "......") 

  ��O���������܂��B

* �ÓI�ϐ���groovy�v���O�����Ԃ̎��s�ŋ��L����܂��B���Ƃ��΁A�V�X�e��
  �v���p�e�B�����L����܂��B

  $ groovy -e "System.setProperty('a','abc')"
  $ groovy -e "println System.getProperty('a')"
  abc

* -l(listen)�I�v�V�����͎g�p�ł��܂���B

* groovy�̓���ɉe����^������ϐ��ɂ��āAgroovyclient�R�}���h����
  �s�����Ƃ��̃V�F���̏�Ԃł͂Ȃ��groovyserver�����s���ꂽ�Ƃ��̊���
  ���̒l���g�p����܂��B���ϐ��̕ύX��groovy�R�}���h�ɉe�����y�ڂ���
  ���ɂ��邽�߂ɂ́Agroovyserver���ċN������K�v������܂��B���ɁA
  CLASSPATH�̎w���groovyserver���N������Ƃ��Ɏw�肷��K�v������܂��B

================
�o�O
================

* groovyclient�R�}���h��Control-C�Ȃǂŋ����I�������ꍇ�ł��AServer���
  ���s�����Groovy�R�[�h�͏I�����܂���B���ɁA�W�����͂�����͑҂��ɂȂ�
  �Ă���悤��Groovy�v���O�����������I�������Ƃ��A�����̓��͑҂��ƂȂ�
  �܂��Bgroovyserver���I��������K�v������܂��B

================
�I�v�V����
================

tcp�̃|�[�g�ԍ��̓f�t�H���g�ł�1961���g�p���܂��B�ύX����ɂ�[TBD]
GroovyServer�̋N���I�v�V�����͈ȉ��̂Ƃ���B

   -v  �璷�\���B�f�o�b�O���Ȃǂ�\�����܂��B
   -k �N�����Ă���GroovyServer���I�������܂��B
   -r �N�����Ă���GroovyServer���ċN�����܂�(��~+�N��)�B

================
���O�t�@�C��
================

~/.groovy/groovyserver/<�v���Z�XID>.log

��groovyserver�̃��O�t�@�C�����o�͂���܂��B

================
�Z�L�����e�B
================

groovyserver�ɂ́A���̃}�V�������groovyclient�R�}���h���g���Đڑ�����
���Ƃ͂ł��܂���B�������A�F�؂͂����Ă��Ȃ��̂ŁA�����}�V�����g���Ă�
�鑼�̃��[�U�[����groovyserver�ɐڑ�����Ď��s����Ă��܂��\��������
�܂��B���̍ۂɎ��s���s����̂́Agroovyserver���N���������[�U�[�̌���
�Ŏ��s����܂��B


